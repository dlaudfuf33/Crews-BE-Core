package org.baas.baascore.service;

import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.request.TransactionDetailRequest;
import org.baas.baascore.dto.request.TransferRequest;
import org.baas.baascore.dto.request.TransferStatesRequest;
import org.baas.baascore.dto.response.TransactionDetailResponse;
import org.baas.baascore.dto.response.TransactionHistoryResponse;
import org.baas.baascore.dto.response.TransferResponse;
import org.baas.baascore.dto.response.TransferStatesResponse;
import org.baas.baascore.exception.CustomException;
import org.baas.baascore.exception.ErrorCode;
import org.baas.baascore.model.Account;
import org.baas.baascore.model.Customer;
import org.baas.baascore.model.TransactionHistory;
import org.baas.baascore.repository.AccountRepository;
import org.baas.baascore.repository.CustomerRepository;
import org.baas.baascore.repository.TransactionHistoryRepository;
import org.baas.baascore.util.AccountType;
import org.baas.baascore.util.StatusType;
import org.baas.baascore.util.TranType;
import org.hibernate.exception.LockTimeoutException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreTransactionService {
    private static final int MAX_RETRIES = 3;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionExecutionService transactionExecutionService;
    private final AccountHelper accountHelper;
    private final TransactionHistoryService transactionHistoryService;

    public TransactionDetailResponse transactionDetail(TransactionDetailRequest transactionDetailRequest) {
        Account account = accountRepository.findByFintechUseNum(transactionDetailRequest.getFintechUseNum()).orElseThrow(() -> new CustomException(ErrorCode.ACCOUNTNUMBER_NOT_FOUND));
        Customer customer = customerRepository.findByCi(transactionDetailRequest.getCi()).orElseThrow(() -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND));
        if (!customer.equals(account.getCustomer()) && account.getAccountType().equals(AccountType.PERSONAL)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_EQUALS);
        }
        Integer selectPeriod = transactionDetailRequest.getSelectPeriod();
        if (!(selectPeriod == 1 || selectPeriod == 3 || selectPeriod == 6 || selectPeriod == 9))
            throw new CustomException(ErrorCode.WRONG_PERIOD);
        LocalDateTime filteredDate = LocalDateTime.now().minusMonths(selectPeriod);
        String transactionType = transactionDetailRequest.getTransactionType();
        String order = transactionDetailRequest.getOrder();
        List<TransactionHistory> list = getTransactionHistories(transactionType, account, filteredDate, order);
        List<TransactionHistoryResponse> historyDtoList = list.stream().map(TransactionHistoryResponse::from).toList();
        return TransactionDetailResponse.builder().tranList(historyDtoList).accountNumber(account.getAccountNumber()).productName(account.getProduct().getProductName()).balance(account.getBalance()).bankCode(account.getBank().getBankCode()).bankName(account.getBank().getBankName()).build();
    }

    private List<TransactionHistory> getTransactionHistories(String transactionType, Account account, LocalDateTime filteredDate, String order) {
        List<TransactionHistory> list;
        final String ALL = "ALL";
        final String DESC = "DESC";
        final String DEPOSIT = "DEPOSIT";
        final String WITHDRAW = "WITHDRAW";
        if (transactionType.equalsIgnoreCase(ALL)) {
            list = transactionHistoryRepository.findTransactionHistoryAllTranType(account, filteredDate);
            if (order.equalsIgnoreCase(DESC)) list.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

        } else if (transactionType.equalsIgnoreCase(DEPOSIT) || transactionType.equalsIgnoreCase(WITHDRAW)) {
            list = transactionHistoryRepository.findTransactionHistorySelectedTranType(account, filteredDate, TranType.valueOf(transactionType.toUpperCase()));
            if (order.equalsIgnoreCase(DESC)) list.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
        } else {
            throw new CustomException(ErrorCode.WRONG_TRANSACTION_TYPE);
        }
        return list;
    }

    public TransferResponse transfer(TransferRequest transferRequest) {
        Map<String, Account> mappedAccounts = accountHelper.fetchAndMapAccounts(transferRequest.getFinUseNum(), transferRequest.getRecvAccountNum(), false // 단순 조회
        );

        Account fromAccount = mappedAccounts.get("fromAccount");
        Account toAccount = mappedAccounts.get("toAccount");


        for (int retry = 0; retry < MAX_RETRIES; retry++) {
            try {
                // 별도 서비스로 분리된 트랜잭션 처리 호출
                return transactionExecutionService.execute(transferRequest);
            } catch (PessimisticLockException | LockTimeoutException e) {
                log.warn("재시도 ... ");
                handleRetry(retry);
            } catch (CustomException e) {
                transactionHistoryService.issueHistory(transferRequest, fromAccount, toAccount, StatusType.FAIL);
                if (e.getErrorCode() == ErrorCode.INSUFFICIENT_BALANCE) {
                    log.error("잔액 부족으로 거래 실패: {}", e.getMessage());
                    throw new CustomException(e.getErrorCode());
                }
                break; // 재시도하지 않고 즉시 실패
            }
        }
        transactionHistoryService.issueHistory(transferRequest, fromAccount, toAccount, StatusType.FAIL);
        throw new CustomException(ErrorCode.TRANSFER_TRIED_FAILED);
    }

    private void handleRetry(int retry) {
        long backoff = Math.min((long) Math.pow(2, retry) * 1000, 3000); // 지수 백오프
        log.warn("이체 재시도 - 시도 횟수: {}, 대기 시간: {}ms", retry + 1, backoff);
        try {
            Thread.sleep(backoff);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.TRANSFER_INTERRUPTED, e);
        }
    }

    /**
     * 거래내역 Id , 핀테크이용번호 로 거래상태를 반환 합니다.
     *
     * @return TransferStatesResponseDto
     */
    public TransferStatesResponse getTransactionStatus(TransferStatesRequest transferStatesRequest) {
        return TransferStatesResponse.from(transactionHistoryRepository.findByCoreTransactionIdAndAccount_FintechUseNum(transferStatesRequest.getHistoryId(), transferStatesRequest.getFinUseNum()).orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND)));
    }
}
