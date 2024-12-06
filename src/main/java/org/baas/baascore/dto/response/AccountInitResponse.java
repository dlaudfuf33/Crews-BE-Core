package org.baas.baascore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.baas.baascore.model.Account;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class AccountInitResponse {
    private int index;
    private String customerName;
    private String bankCode;
    private String bankImage;
    private String productName;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public static AccountInitResponse of(Account account, int index) {
        return AccountInitResponse.builder()
                .index(index)
                .customerName(account.getCustomer().getName())
                .bankImage(account.getBank().getBankImage())
                .bankCode(account.getBank().getBankCode())
                .productName(account.getProduct().getProductName())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt().toLocalDate())
                .updatedAt(account.getUpdatedAt().toLocalDate())
                .build();
    }
}
