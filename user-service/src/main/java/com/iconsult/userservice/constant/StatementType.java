package com.iconsult.userservice.constant;

public enum StatementType {
    MINI("0"),
    DATE_RANGE("1");

    private final String value;

    StatementType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public static StatementType fromValue(String value) {
        for (StatementType type : StatementType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid Type: " + value);
    }
}
