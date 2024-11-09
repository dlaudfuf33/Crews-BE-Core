package org.baas.baascore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CardReissuedRequest {

    @NotBlank
    private String identityCode;

    @NotBlank
    private String fintechUseNum;

    @NotBlank
    private String cardNumber;
}
