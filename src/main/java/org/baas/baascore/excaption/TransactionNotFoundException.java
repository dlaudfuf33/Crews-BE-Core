package org.baas.baascore.excaption;

import lombok.Getter;

@Getter
public class TransactionNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public TransactionNotFoundException() {
        super(ErrorCode.TRANSACTION_NOT_FOUND.getMessage());
        this.errorCode = ErrorCode.TRANSACTION_NOT_FOUND;
    }
}
