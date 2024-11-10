package org.baas.baascore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseDto {

    /**
     * 거래의 고유 식별자 (거래 내역 ID)
     */
    private Long historyId;

    /**
     * 수신자의 이름
     */
    private String recvName;

    /**
     * 수신자의 은행 코드
     */
    private String recvBankcode;

    /**
     * 수신자의 계좌 번호
     */
    private String recvAccountNum;

    /**
     * 이체된 금액
     */
    private BigDecimal amount;

    /**
     * 이체 후 남은 잔액 (출금 계좌)
     */
    private BigDecimal afterAmt;


    /**
     * TransferResponseDto 생성자 설명:
     * 이체 거래가 성공적으로 완료된 후 클라이언트에게 필요한 정보를 전달하는 DTO입니다.
     *
     * @param historyId      거래 내역 ID
     * @param recvName       수신자 이름
     * @param recvBankcode   수신자 은행 코드
     * @param recvAccountNum 수신자 계좌 번호
     * @param amount         이체된 금액
     * @param afterAmt       이체 후 남은 출금 계좌 잔액
     */
}