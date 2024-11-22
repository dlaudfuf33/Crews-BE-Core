package org.baas.baascore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.baas.baascore.model.Account;
import org.baas.baascore.util.AccountType;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountOneResponse {
    private String memberName;
    private String ci;
    private AccountType accountType;
    private String bankCode;
    private String bankName;
    private String accountNumber;
    private String fintechUseNum;
    private String productName;
    private BigDecimal balance;

    public static AccountOneResponse from(Account account){
        return AccountOneResponse.builder()
                .memberName(account.getCustomer().getName())
                .ci(account.getCustomer().getCi())
                .accountType(account.getAccountType())
                .bankCode(account.getBank().getBankCode())
                .bankName(account.getBank().getBankName())
                .accountNumber(account.getAccountNumber())
                .fintechUseNum(account.getFintechUseNum())
                .productName(account.getProduct().getProductName())
                .balance(account.getBalance())
                .build();
    }
}
