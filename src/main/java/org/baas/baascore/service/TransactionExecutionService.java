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

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionExecutionService {
    private final AccountRepository accountRepository;
    private final TransactionHistoryService transactionHistoryService;


    @Transactional(timeout = 10)
    public TransferResponse execute(TransferRequest transferRequest) {
        // 계좌 락 점유
        Account withdrawAccount = accountRepository.findByAccountNumberWithLock(transferRequest.getRecvAccountNum())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
        Account depositAccount = accountRepository.findByFintechUseNumWithLock(transferRequest.getFinUseNum())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));


        // 금액 차감 및 증가
        withdrawAccount.subtractFromBalance(transferRequest.getAmt());
        depositAccount.addToBalance(transferRequest.getAmt());
        // 거래 내역 생성
        TransactionHistory[] histories = transactionHistoryService.issueHistory(transferRequest, withdrawAccount, depositAccount, StatusType.SUCCESS);
        TransactionHistory withdrawHistory = histories[0];
        TransactionHistory depositHistory = histories[1];
        accountRepository.saveAll(List.of(withdrawAccount, depositAccount));
        log.info("withdraw & deposit - amount: {} withdraw balance: {}, deposit balance: {}", transferRequest.getAmt(), withdrawAccount.getBalance(), depositAccount.getBalance());

        log.info("success transfer - update history-status");
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
}
