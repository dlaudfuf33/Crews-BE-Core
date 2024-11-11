package org.baas.baascore.dto;

import lombok.Builder;
import lombok.Getter;
import org.baas.baascore.model.TransactionHistory;
import org.baas.baascore.util.AccountType;
import org.baas.baascore.util.TranType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionHistoryDto {
    private String memberName;
    private String identityCode;
    private AccountType accountType;
    private String bankCode;
    private String bankName;
    private String accountNumber;
    private TranType tranType;
    private LocalDateTime transactionTime;
    private String description;
    private BigDecimal tranAmount;
    private BigDecimal afterBalanceAmount;
    private String withdrawerName;

    public static TransactionHistoryDto from(TransactionHistory transactionHistory){
        return TransactionHistoryDto.builder()
                .memberName(transactionHistory.getAccount().getCustomer().getName())
                .identityCode(transactionHistory.getAccount().getCustomer().getIdentityCode())
                .accountType(transactionHistory.getAccount().getAccountType())
                .bankCode(transactionHistory.getAccount().getBank().getBankCode())
                .bankName(transactionHistory.getAccount().getBank().getBankName())
                .accountNumber(transactionHistory.getAccount().getAccountNumber())
                .tranType(transactionHistory.getTranType())
                .transactionTime(transactionHistory.getCreatedAt())
                .description(transactionHistory.getDescription())
                .tranAmount(transactionHistory.getTranAmt())
                .afterBalanceAmount(transactionHistory.getAfterBalanceAmt())
                .withdrawerName(transactionHistory.getCounterpartyName())
                .build();
    }
}
