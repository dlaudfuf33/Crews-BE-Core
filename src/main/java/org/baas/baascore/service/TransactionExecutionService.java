package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.request.TransferRequest;
import org.baas.baascore.dto.response.TransferResponse;
import org.baas.baascore.exception.CustomException;
import org.baas.baascore.exception.ErrorCode;
import org.baas.baascore.model.Account;
import org.baas.baascore.model.TransactionHistory;
import org.baas.baascore.repository.AccountRepository;
import org.baas.baascore.util.StatusType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionExecutionService {
    private final AccountRepository accountRepository;
    private final TransactionHistoryService transactionHistoryService;

    @Transactional(timeout = 10)
    public TransferResponse execute(TransferRequest transferRequest) {
        // 계좌 락 점유 및 매핑
        Map<String, Account> mappedAccounts = fetchAndMapAccounts(
                transferRequest.getFinUseNum(),
                transferRequest.getRecvAccountNum(),
                true // 락 점유
        );

        Account fromAccount = mappedAccounts.get("fromAccount");
        Account toAccount = mappedAccounts.get("toAccount");

        // 금액 차감 및 증가
        fromAccount.subtractFromBalance(transferRequest.getAmt());
        toAccount.addToBalance(transferRequest.getAmt());
        // 거래 내역 생성
        TransactionHistory[] histories = transactionHistoryService.issueHistory(transferRequest, fromAccount, toAccount, StatusType.SUCCESS);
        TransactionHistory withdrawHistory = histories[0];
        TransactionHistory depositHistory = histories[1];
        accountRepository.saveAll(List.of(fromAccount, toAccount));
        log.info("출금 및 입금 완료 - 출금 계좌 잔액: {}, 입금 계좌 잔액: {}", fromAccount.getBalance(), toAccount.getBalance());

        log.info("이체 거래 성공 - 상태 업데이트");
        return TransferResponse.builder()
                .historyId(withdrawHistory.getCoreTransaction().getId())
                .recvName(depositHistory.getAccount().getCustomer().getName())
                .recvBankcode(depositHistory.getAccount().getBank().getBankCode())
                .recvAccountNum(depositHistory.getAccount().getAccountNumber())
                .amount(withdrawHistory.getTranAmt())
                .afterAmt(withdrawHistory.getAccount().getBalance())
                .transactionTime(withdrawHistory.getCreatedAt())
                .build();
    }

    private Map<String, Account> fetchAndMapAccounts(String fromFintechUseNum, String toAccountNumber, boolean withLock) {
        List<Account> accounts = withLock
                ? accountRepository.findAccountsForTransferWithLock(fromFintechUseNum, toAccountNumber)
                : accountRepository.findAccountsForTransfer(fromFintechUseNum, toAccountNumber);

        if (accounts.size() != 2) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND, "출금 또는 입금 계좌를 찾을 수 없습니다.");
        }

        Account fromAccount = accounts.stream()
                .filter(a -> a.getFintechUseNum().equals(fromFintechUseNum))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.WITHDRAW_ACCOUNT_NOT_FOUND));

        Account toAccount = accounts.stream()
                .filter(a -> a.getAccountNumber().equals(toAccountNumber))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.DEPOSIT_ACCOUNT_NOT_FOUND));

        return Map.of("fromAccount", fromAccount, "toAccount", toAccount);
    }
}
