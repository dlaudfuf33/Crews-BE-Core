package org.baas.baascore.excaption;

import lombok.Getter;

@Getter
public class TransferFailedException extends RuntimeException {
    private final ErrorCode errorCode;

    public TransferFailedException(Throwable cause) {
        super(ErrorCode.TRANSFER_FAILED.getMessage(), cause);
        this.errorCode = ErrorCode.TRANSFER_FAILED;
    }
}