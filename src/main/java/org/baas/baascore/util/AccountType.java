package org.baas.baascore.util;

public enum AccountType {
    PERSONAL("개인"),
    CREW("모임"),
    CORPORATE("법인");
    private final String type;

    AccountType(String type) {
        this.type = type;
    }
}
