package org.baas.baascore.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class TransactionDetailResponse {

    @Builder.Default
    private List<TransactionHistoryDto> tranList = new ArrayList<>();
}
