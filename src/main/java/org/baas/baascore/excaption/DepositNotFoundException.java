package org.baas.baascore.excaption;

import lombok.Getter;

@Getter
public class DepositNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public DepositNotFoundException() {
        super(ErrorCode.DEPOSIT_ACCOUNT_NOT_FOUND.getMessage());
        this.errorCode = ErrorCode.DEPOSIT_ACCOUNT_NOT_FOUND;
    }
}