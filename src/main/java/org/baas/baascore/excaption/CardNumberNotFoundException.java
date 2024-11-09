package org.baas.baascore.excaption;

import lombok.Getter;

@Getter
public class CardNumberNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public CardNumberNotFoundException() {
        super(ErrorCode.CARDNUMBER_NOT_FOUND.getMessage());
        this.errorCode = ErrorCode.CARDNUMBER_NOT_FOUND;
    }
}
