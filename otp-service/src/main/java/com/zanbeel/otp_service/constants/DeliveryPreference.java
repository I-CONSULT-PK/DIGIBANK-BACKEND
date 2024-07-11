package com.zanbeel.otp_service.constants;

public enum DeliveryPreference {

    BOTH("0"),
    EMAIL("1"),
    SMS("2");

    private final String value;

    DeliveryPreference(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DeliveryPreference fromValue(String value) {
        for (DeliveryPreference method : DeliveryPreference.values()) {
            if (method.value.equals(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

}