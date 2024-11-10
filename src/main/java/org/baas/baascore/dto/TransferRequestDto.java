package org.baas.baascore.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransferRequestDto {

    /**
     * 출금 계좌의 핀테크 이용 번호
     */
    private final String finUseNum;

    /**
     * 입금할 계좌 번호
     */
    private final String recvAccountNum;

    /**
     * 이체할 금액
     */
    private final BigDecimal amt;

    /**
     * 이체 설명
     */
    private final String description;

    public TransferRequestDto(String finUseNum, String recvAccountNum, BigDecimal amt, String description) {
        this.finUseNum = finUseNum;
        this.recvAccountNum = recvAccountNum;
        this.amt = amt;
        this.description = description;
    }
}