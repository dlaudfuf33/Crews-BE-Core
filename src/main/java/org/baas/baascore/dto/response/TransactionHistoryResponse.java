package org.baas.baascore.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baas.baascore.model.TransactionHistory;
import org.baas.baascore.util.TranType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionHistoryResponse {

    private String counterpartyBankCode;
    private String counterpartyAccountNum;
    private TranType tranType;
    private LocalDateTime transactionTime;
    private String description;
    private BigDecimal tranAmount;
    private BigDecimal afterBalanceAmount;
    private String withdrawerName;

    public static TransactionHistoryResponse from(TransactionHistory transactionHistory){
        return TransactionHistoryResponse.builder()
                .counterpartyBankCode(transactionHistory.getCounterpartyBankCode())
                .counterpartyAccountNum(transactionHistory.getCounterpartyAccountNum())
                .tranType(transactionHistory.getTranType())
                .transactionTime(transactionHistory.getCreatedAt())
                .description(transactionHistory.getDescription())
                .tranAmount(transactionHistory.getTranAmt())
                .afterBalanceAmount(transactionHistory.getAfterBalanceAmt())
                .withdrawerName(transactionHistory.getCounterpartyName())
                .build();
    }
}
