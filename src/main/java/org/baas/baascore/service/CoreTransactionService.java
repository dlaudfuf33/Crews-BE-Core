package org.baas.baascore.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.*;
import org.baas.baascore.excaption.*;
import org.baas.baascore.model.Account;
import org.baas.baascore.model.CoreTransaction;
import org.baas.baascore.model.Customer;
import org.baas.baascore.model.TransactionHistory;
import org.baas.baascore.repository.AccountRepository;
import org.baas.baascore.repository.CoreTransactionRepository;
import org.baas.baascore.repository.CustomerRepository;
import org.baas.baascore.repository.TransactionHistoryRepository;
import org.baas.baascore.util.StatusType;
import org.baas.baascore.util.TranType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreTransactionService {
    private final CoreTransactionRepository coreTransactionRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public TransactionDetailResponse transactionDetail(TransactionDetailRequest transactionDetailRequest) {
        Account account = accountRepository.findByFintechUseNum(transactionDetailRequest.getFintechUseNum()).orElseThrow(
                () -> new CustomException(ErrorCode.ACCOUNTNUMBER_NOT_FOUND)
        );
        Customer customer = customerRepository.findByCi(transactionDetailRequest.getCi()).orElseThrow(
                () -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND)
        );
        if (!customer.equals(account.getCustomer()))
            throw new CustomException(ErrorCode.MEMBER_NOT_EQUALS);
        Integer selectPeriod = transactionDetailRequest.getSelectPeriod();
        if (!(selectPeriod == 1 || selectPeriod == 3 || selectPeriod == 6 || selectPeriod == 9))
            throw new CustomException(ErrorCode.WRONG_PERIOD);
        LocalDateTime filteredDate = LocalDateTime.now().minusMonths(selectPeriod);
        String transactionType = transactionDetailRequest.getTransactionType();
        String order = transactionDetailRequest.getOrder();
        List<TransactionHistory> list = getTransactionHistories(transactionType, account, filteredDate, order);
        List<TransactionHistoryDto> historyDtoList = list.stream().map(TransactionHistoryDto::from).toList();
        return TransactionDetailResponse.builder().tranList(historyDtoList).build();
    }

    private List<TransactionHistory> getTransactionHistories(String transactionType, Account account, LocalDateTime filteredDate, String order) {
        List<TransactionHistory> list;
        final String ALL = "ALL";
        final String DESC = "DESC";
        final String DEPOSIT = "DEPOSIT";
        final String WITHDRAW = "WITHDRAW";
        if (transactionType.equalsIgnoreCase(ALL)) {
            list = transactionHistoryRepository.findTransactionHistoryAllTranType(account, filteredDate);
            if (order.equalsIgnoreCase(DESC))
                list.sort((o1, o2) ->
                        o2.getCreatedAt().compareTo(o1.getCreatedAt()));

        } else if (transactionType.equalsIgnoreCase(DEPOSIT) || transactionType.equalsIgnoreCase(WITHDRAW)) {
            list = transactionHistoryRepository.findTransactionHistorySelectedTranType(account, filteredDate, TranType.valueOf(transactionType.toUpperCase()));
            if (order.equalsIgnoreCase(DESC))
                list.sort((o1, o2) ->
                        o2.getCreatedAt().compareTo(o1.getCreatedAt()));
        } else {
            throw new CustomException(ErrorCode.WRONG_TRANSACTION_TYPE);
        }
        return list;
    }

    /**
     * 이체 거래를 처리하고 성공 여부에 따라 거래 내역 상태를 업데이트합니다.
     *
     * @param transferRequestDto 이체 요청 정보를 담고 있는 DTO
     * @return 이체 결과를 담고 있는 TransferResponseDto
     */
    @Transactional
    public TransferResponseDto transfer(TransferRequestDto transferRequestDto) {
        log.info("이체 거래 시작 - 출금 계좌: {}, 입금 계좌: {}, 금액: {}",
                transferRequestDto.getFinUseNum(), transferRequestDto.getRecvAccountNum(), transferRequestDto.getAmt());

        // 출금 계좌와 입금 계좌 찾기 (각각 한 번씩만 조회)
        Account fromAccount = accountRepository.findByFintechUseNumForUpdate(transferRequestDto.getFinUseNum())
                .orElseThrow(() -> new CustomException(ErrorCode.WITHDRAW_ACCOUNT_NOT_FOUND));
        Account toAccount = accountRepository.findByAccountNumberForUpdate(transferRequestDto.getRecvAccountNum())
                .orElseThrow(() -> new CustomException(ErrorCode.DEPOSIT_ACCOUNT_NOT_FOUND));

        // 거래 내역 생성 (송금 및 수신 내역 동시에 생성)
        TransactionHistory[] histories = issueHistory(transferRequestDto, fromAccount, toAccount);
        TransactionHistory withdrawHistory = histories[0];
        TransactionHistory depositHistory = histories[1];

        try {
            // 출금 및 입금 처리
            fromAccount.subtractFromBalance(transferRequestDto.getAmt());
            toAccount.addToBalance(transferRequestDto.getAmt());

            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
            log.info("출금 및 입금 완료 - 출금 계좌 잔액: {}, 입금 계좌 잔액: {}", fromAccount.getBalance(), toAccount.getBalance());

            // 거래 내역 상태 업데이트 (성공)
            markTransactionSuccess(withdrawHistory, depositHistory);
            log.info("이체 거래 성공 - 상태 업데이트 완료");
            return TransferResponseDto.builder()
                    .historyId(withdrawHistory.getCoreTransaction().getId())
                    .recvName(depositHistory.getAccount().getCustomer().getName())
                    .recvBankcode(depositHistory.getAccount().getBank().getBankCode())
                    .recvAccountNum(depositHistory.getAccount().getAccountNumber())
                    .amount(withdrawHistory.getTranAmt())
                    .afterAmt(withdrawHistory.getAccount().getBalance())
                    .build();// 응답 생성
        } catch (CustomException e) {
            if (e.getErrorCode() == ErrorCode.INSUFFICIENT_BALANCE) {
                markTransactionFail(withdrawHistory, depositHistory);
                log.error("잔액 부족으로 이체 실패 - 출금 계좌: {}, 금액: {}",
                        transferRequestDto.getFinUseNum(), transferRequestDto.getAmt());
            }
            throw e;  // 예외 재발생
        } catch (Exception e) {
            // 기타 예외에 대해 실패 처리
            markTransactionFail(withdrawHistory, depositHistory);
            log.error("이체 거래 처리 중 오류 발생 - 출금 계좌: {}, 입금 계좌: {}, 금액: {}",
                    transferRequestDto.getFinUseNum(), transferRequestDto.getRecvAccountNum(), transferRequestDto.getAmt(), e);
            throw new CustomException(ErrorCode.TRANSFER_FAILED, e);
        }
    }

    /**
     * 이체 요청에 따른 거래 내역을 생성합니다. (송금 내역과 수신 내역)
     *
     * @param transferRequestDto 이체 요청 정보를 담고 있는 DTO
     * @param fromAccount        송금하는 계좌 정보
     * @param toAccount          수신하는 계좌 정보
     * @return 송금 및 수신 내역을 포함하는 TransactionHistory 배열
     */
    private TransactionHistory[] issueHistory(TransferRequestDto transferRequestDto, Account fromAccount, Account toAccount) {
        log.info("거래 내역 생성 - 출금 계좌: {}, 입금 계좌: {}", fromAccount.getId(), toAccount.getId());

        // CoreTransaction 인스턴스를 각각 생성하여 PENDING 상태로 설정
        CoreTransaction tradeTrx = createPendingTransaction();

        // 출금 거래 내역 생성
        TransactionHistory withdrawHistory = TransactionHistory.builder()
                .account(fromAccount)
                .tranType(TranType.WITHDRAW)
                .tranAmt(transferRequestDto.getAmt())
                .afterBalanceAmt(fromAccount.getBalance().subtract(transferRequestDto.getAmt()))
                .countryAccount(toAccount)
                .coreTransaction(tradeTrx)
                .description(transferRequestDto.getDescription())
                .build();

        // 입금 거래 내역 생성
        TransactionHistory depositHistory = TransactionHistory.builder()
                .account(toAccount)
                .tranType(TranType.DEPOSIT)
                .tranAmt(transferRequestDto.getAmt())
                .afterBalanceAmt(toAccount.getBalance().add(transferRequestDto.getAmt()))
                .countryAccount(fromAccount)
                .coreTransaction(tradeTrx)
                .description(transferRequestDto.getDescription())
                .build();

        transactionHistoryRepository.saveAndFlush(withdrawHistory);
        transactionHistoryRepository.saveAndFlush(depositHistory);
        log.info("거래 내역 생성 완료 - 출금 내역 ID: {}, 입금 내역 ID: {}", withdrawHistory.getId(), depositHistory.getId());

        return new TransactionHistory[]{withdrawHistory, depositHistory};
    }

    /**
     * PENDING 상태의 CoreTransaction을 생성합니다.
     *
     * @return 생성된 CoreTransaction 객체
     */
    private CoreTransaction createPendingTransaction() {
        CoreTransaction transaction = CoreTransaction.builder().status(StatusType.PENDING).build();
        coreTransactionRepository.save(transaction);
        log.info("PENDING 상태의 CoreTransaction 생성 완료 - ID: {}", transaction.getId());
        return transaction;
    }

    /**
     * 거래 내역 상태를 성공으로 업데이트합니다.
     */
    private void markTransactionSuccess(TransactionHistory withdrawHistory, TransactionHistory depositHistory) {
        withdrawHistory.getCoreTransaction().markSuccess();
        depositHistory.getCoreTransaction().markSuccess();
        transactionHistoryRepository.saveAll(List.of(withdrawHistory, depositHistory));
    }

    /**
     * 거래 내역 상태를 실패로 업데이트합니다.
     */
    private void markTransactionFail(TransactionHistory withdrawHistory, TransactionHistory depositHistory) {
        withdrawHistory.getCoreTransaction().markFail();
        depositHistory.getCoreTransaction().markFail();
        transactionHistoryRepository.saveAll(List.of(withdrawHistory, depositHistory));
    }

    /**
     * 거래내역 Id , 핀테크이용번호 로 거래상태를 반환 합니다.
     *
     * @return TransferStatesResponseDto
     */
    public TransferStatesResponseDto getTransactionStatus(TransferStatesRequestDto transferStatesRequestDto) {
        return TransferStatesResponseDto.of(
                transactionHistoryRepository
                        .findByCoreTransactionIdAndAccount_FintechUseNum(
                                transferStatesRequestDto.getHistoryId(),
                                transferStatesRequestDto.getFinUseNum())
                        .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND))
        );
    }
}
