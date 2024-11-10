package org.baas.baascore.util;

public enum StatusType {
    PENDING("대기"),
    SUCCESS("성공"),
    FAIL("실패");

    private final String type;

    StatusType(String type) {
        this.type = type;
    }
}
