package org.baas.baascore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SubcriptionsRequest {
    @NotBlank
    private String companyName;

    @NotBlank
    private String productName;

    @NotBlank
    private String businessNum;
}
