package org.baas.baascore.dto;

import lombok.Getter;

@Getter
public class IssueApikeyRequestDto {
    private Long bankId;
    private String productName;
    private String businessNum;
    private String companyName;
    private String accountNumber;

}

