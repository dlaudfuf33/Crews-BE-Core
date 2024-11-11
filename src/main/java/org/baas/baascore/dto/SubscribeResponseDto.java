package org.baas.baascore.dto;

import lombok.Getter;

@Getter
public class SubscribeResponseDto {

    /**
     * 액세스 키
     */
    private final String accessKey;

    /**
     * 비밀 키
     */
    private final String secretKey;

    public SubscribeResponseDto(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
}
