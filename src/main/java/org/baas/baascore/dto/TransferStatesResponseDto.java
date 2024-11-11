package org.baas.baascore.dto;

import lombok.Builder;
import lombok.Getter;
import org.baas.baascore.model.TransactionHistory;
import org.baas.baascore.util.StatusType;

import java.time.LocalDateTime;

@Getter
@Builder
public class TransferStatesResponseDto {
    /**
     * 트랜젝션의 고유 식별자 (트랜젝션 ID)
     */
    private Long tranId;

    /**
     * 거래의 고유 식별자 (거래 내역 ID)
     */
    private Long historyId;
    /**
     * 발생 일시 (createdAt)
     */
    private LocalDateTime reqAt;
    /**
     * 현 상태 (status)
     */
    private StatusType state;


    public static TransferStatesResponseDto of(TransactionHistory transactionHistory) {
        return TransferStatesResponseDto.builder()
                .tranId(transactionHistory.getCoreTransaction().getId())
                .historyId(transactionHistory.getId())
                .reqAt(transactionHistory.getCreatedAt())
                .state(transactionHistory.getCoreTransaction().getStatus())
                .build();
    }
}