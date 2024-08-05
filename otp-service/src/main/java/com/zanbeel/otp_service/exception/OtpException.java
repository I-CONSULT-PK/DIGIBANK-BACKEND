package com.zanbeel.otp_service.exception;

import com.zanbeel.otp_service.constants.Error;
import lombok.Getter;

@Getter
public class OtpException extends RuntimeException {

    private final Error error;

    public OtpException(Error error) {
        super(error.getMessage());
        this.error = error;
    }
}