package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.request.TransferRequest;
import org.baas.baascore.model.Account;
import org.baas.baascore.model.CoreTransaction;
import org.baas.baascore.model.TransactionHistory;
import org.baas.baascore.repository.CoreTransactionRepository;
import org.baas.baascore.repository.TransactionHistoryRepository;
import org.baas.baascore.util.StatusType;
import org.baas.baascore.util.TranType;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionHistoryService {
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final CoreTransactionRepository coreTransactionRepository;

    /**
     * 이체 요청에 따른 거래 내역을 생성합니다. (송금 내역과 수신 내역)
     *
     * @param transferRequest 이체 요청 정보를 담고 있는 DTO
     * @param fromAccount     송금하는 계좌 정보
     * @param toAccount       수신하는 계좌 정보
     * @return 송금 및 수신 내역을 포함하는 TransactionHistory 배열
     */
    public TransactionHistory[] issueHistory(TransferRequest transferRequest, Account fromAccount, Account toAccount, StatusType statusType) {
        log.info("거래 내역 생성 - 출금 계좌: {}, 입금 계좌: {}", fromAccount.getId(), toAccount.getId());

        String descriptionTmp = transferRequest.getDescription() == null ? "" : transferRequest.getDescription();

        // CoreTransaction 인스턴스를 각각 생성하여 PENDING 상태로 설정
        CoreTransaction tradeTrx = createTransaction(statusType);

        // 출금 거래 내역 생성
        TransactionHistory withdrawHistory = TransactionHistory.builder()
                .account(fromAccount)
                .tranType(TranType.WITHDRAW)
                .tranAmt(transferRequest.getAmt())
                .afterBalanceAmt(fromAccount.getBalance().subtract(transferRequest.getAmt()))
                .countryAccount(toAccount)
                .coreTransaction(tradeTrx)
                .description(descriptionTmp)
                .build();

        // 입금 거래 내역 생성
        TransactionHistory depositHistory = TransactionHistory.builder()
                .account(toAccount)
                .tranType(TranType.DEPOSIT)
                .tranAmt(transferRequest.getAmt())
                .afterBalanceAmt(toAccount.getBalance().add(transferRequest.getAmt()))
                .countryAccount(fromAccount)
                .coreTransaction(tradeTrx)
                .description(transferRequest.getDescription())
                .build();

        transactionHistoryRepository.saveAll(List.of(withdrawHistory, depositHistory));
        log.info("거래 내역 생성 완료 - 출금 내역 ID: {}, 입금 내역 ID: {}", withdrawHistory.getId(), depositHistory.getId());
        return new TransactionHistory[]{withdrawHistory, depositHistory};
    }

    /**
     * PENDING 상태의 CoreTransaction을 생성합니다.
     *
     * @return 생성된 CoreTransaction 객체
     */
    private CoreTransaction createTransaction(StatusType statusType) {
        CoreTransaction transaction = CoreTransaction.builder().status(statusType).build();
        coreTransactionRepository.save(transaction);
        log.info("{} 상태의 CoreTransaction 생성 완료 - ID: {}", statusType, transaction.getId());
        return transaction;
    }

}
