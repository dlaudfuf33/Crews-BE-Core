package org.baas.baascore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeRequestDto {

    /**
     * 은행 ID
     */
    private Long bankId;

    /**
     * 상품 이름
     */
    private String productName;

    /**
     * 사업자 번호
     */
    private String businessNum;

    /**
     * 회사 이름
     */
    private String companyName;
}