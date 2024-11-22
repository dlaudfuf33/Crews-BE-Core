package org.baas.baascore.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IssueApikeyResponset {
    private String accessKey;
    private String secretKey;

    public IssueApikeyResponset(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
}
