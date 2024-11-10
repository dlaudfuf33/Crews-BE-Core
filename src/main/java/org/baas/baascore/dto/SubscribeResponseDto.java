package org.baas.baascore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeResponseDto {

    /**
     * 액세스 키
     */
    private String accessKey;

    /**
     * 비밀 키
     */
    private String secretKey;
}