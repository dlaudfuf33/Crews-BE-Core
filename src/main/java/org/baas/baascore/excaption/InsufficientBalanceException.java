package org.baas.baascore.excaption;
import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {
    private final ErrorCode errorCode;

    public InsufficientBalanceException() {
        super(ErrorCode.INSUFFICIENT_BALANCE.getMessage());
        this.errorCode = ErrorCode.INSUFFICIENT_BALANCE;
    }
}

