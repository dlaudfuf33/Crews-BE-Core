package org.baas.baascore.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baas.baascore.model.Card;

import java.time.LocalDateTime;

@Getter
@Builder
public class CardListResponse {
    private String memberName;
    private String bankCode;
    private String bankName;
    private String accountNumber;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String cardNumber;

    public static CardListResponse from(Card card){
        return CardListResponse.builder()
                .memberName(card.getCustomer().getName())
                .bankCode(card.getAccount().getBank().getBankCode())
                .bankName(card.getAccount().getBank().getBankName())
                .accountNumber(card.getAccount().getAccountNumber())
                .createAt(card.getCreatedAt())
                .updateAt(card.getUpdatedAt())
                .cardNumber(card.getCardNumber())
                .build();
    }
}
