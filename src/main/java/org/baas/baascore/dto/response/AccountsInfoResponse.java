package org.baas.baascore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AccountsInfoResponse {
    private int totalCount;
    private List<AccountInitResponse> accounts;
}
