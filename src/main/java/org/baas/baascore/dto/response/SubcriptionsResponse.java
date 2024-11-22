package org.baas.baascore.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baas.baascore.model.Subscribe;

import java.time.LocalDate;

@Getter
@Builder
public class SubcriptionsResponse {
    private String bankCode;
    private String productName;
    private String subscriptionState;
    private LocalDate subscriptionDate;
    private LocalDate expiredDate;

    public static SubcriptionsResponse from(Subscribe subscribe){
        return SubcriptionsResponse.builder()
                .bankCode(subscribe.getBank().getBankCode())
                .productName(subscribe.getProductName())
                .subscriptionState(subscribe.isSubscribed() ? "구독중" :"미구독")
                .subscriptionDate(subscribe.getCreatedAt().toLocalDate())
                .expiredDate(subscribe.getExpireDate().toLocalDate())
                .build();
    }
}
