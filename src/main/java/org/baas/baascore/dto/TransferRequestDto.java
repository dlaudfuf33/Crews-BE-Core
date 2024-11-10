package org.baas.baascore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class TransferRequestDto {

    /**
     * 출금 계좌의 핀테크 이용 번호
     */
    private String finUseNum;

    /**
     * 입금할 계좌 번호
     */
    private String recvAccountNum;

    /**
     * 이체할 금액
     */
    private BigDecimal amt;

    /**
     * 이체 설명
     */
    private String description;
}