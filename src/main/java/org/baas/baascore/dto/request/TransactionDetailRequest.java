package org.baas.baascore.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TransactionDetailRequest {

    @NotBlank
    private String ci;

    @NotBlank
    private String fintechUseNum;

    @NotBlank
    @Max(9)
    private Integer selectPeriod;

    @NotBlank
    private String transactionType;

    @NotBlank
    private String order;
}
