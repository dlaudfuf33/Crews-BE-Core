package org.baas.baascore.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baas.baascore.model.Account;
import org.baas.baascore.util.AccountType;

import java.math.BigDecimal;

@Getter
@Builder
public class MultiFintechNumResponse {
    private int index;
    private String memberName;
    private AccountType accountType;
    private String bankCode;
    private String bankName;
    private String productName;
    private String accountNumber;
    private BigDecimal balance;
    private String fintechUseNum;


    public static MultiFintechNumResponse of(Account account, int index) {
        return MultiFintechNumResponse.builder()
                .index(index)
                .memberName(account.getCustomer().getName())
                .accountType(account.getAccountType())
                .bankCode(account.getBank().getBankCode())
                .bankName(account.getBank().getBankName())
                .productName(account.getProduct().getProductName())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .fintechUseNum(account.getFintechUseNum())
                .build();

    }
}
