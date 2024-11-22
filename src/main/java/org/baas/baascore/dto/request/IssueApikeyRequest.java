package org.baas.baascore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class IssueApikeyRequest {

    @NotBlank
    private String businessNum;

    @NotBlank
    private String companyName;

    @NotBlank
    private String accountNumber;
}

