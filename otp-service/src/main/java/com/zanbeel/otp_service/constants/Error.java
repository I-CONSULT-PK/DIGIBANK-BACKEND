package com.zanbeel.otp_service.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum Error {

    INVALID_REQUEST("E1", "Invalid request", OK),
    OTP_EXPIRED("E2", "Otp has been expired.", OK),
    ACCOUNT_BLOCKED("E3", "Account Blocked", OK),
    INVALID_OTP("E4", "Invalid otp", OK),
    OTP_SEND_LIMIT_EXCEEDED("E5", "Send otp limit exceed", OK);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
