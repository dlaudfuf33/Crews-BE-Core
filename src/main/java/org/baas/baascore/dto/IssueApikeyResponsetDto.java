package org.baas.baascore.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IssueApikeyResponsetDto {
    private String accessKey;
    private String secretKey;

    public IssueApikeyResponsetDto(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
}
