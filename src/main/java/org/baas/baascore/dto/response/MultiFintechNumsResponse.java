package org.baas.baascore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MultiFintechNumsResponse {
    private int totalCount;
    private List<MultiFintechNumResponse> accounts;
}
