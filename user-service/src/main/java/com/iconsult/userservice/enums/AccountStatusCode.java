package com.iconsult.userservice.enums;

public enum AccountStatusCode {
    ACTIVE("00"),
    TEMP_BLOCK("01"),
    PERMANENT_BLOCK("02");

    private final String code;

    AccountStatusCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
