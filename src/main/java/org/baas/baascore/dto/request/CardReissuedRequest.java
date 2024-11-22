package org.baas.baascore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CardReissuedRequest {

    @NotBlank
    private String ci;

    @NotBlank
    private String fintechUseNum;

    @NotBlank
    private String cardNumber;
}
