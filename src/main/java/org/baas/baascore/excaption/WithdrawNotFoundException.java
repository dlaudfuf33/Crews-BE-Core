package org.baas.baascore.excaption;

import lombok.Getter;

@Getter
public class WithdrawNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public WithdrawNotFoundException() {
        super(ErrorCode.WITHDRAW_ACCOUNT_NOT_FOUND.getMessage());
        this.errorCode = ErrorCode.WITHDRAW_ACCOUNT_NOT_FOUND;
    }
}