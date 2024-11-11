package org.baas.baascore.dto;

import lombok.Getter;

@Getter
public class TransferStatesRequestDto {

    /**
     * 거래의 고유 식별자 (거래 내역 ID)
     */
    private Long historyId;

    /**
     * 해당 계좌의 핀테크 이용 번호
     */
    private String finUseNum;
}