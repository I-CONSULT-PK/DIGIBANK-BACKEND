package com.zanbeel.otp_service.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SMSCategory
{
    VERIFY_MOBILE_DEVICE("Verify Mobile Device");

    private String value;
}
