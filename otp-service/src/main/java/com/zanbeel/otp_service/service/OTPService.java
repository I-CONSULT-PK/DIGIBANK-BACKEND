package com.zanbeel.otp_service.service;

import com.zanbeel.otp_service.config.CustomApiResponse;
import com.zanbeel.otp_service.domain.OTP;
import com.zanbeel.otp_service.dto.EmailOTPSendDto;
import com.zanbeel.otp_service.dto.EmailOTPVerifyDto;

import java.util.List;

public interface OTPService {


    List<OTP> findByMobileNumberAndIsExpired(String mobileNumber, Boolean isExpired);
    OTP findTopByMobileNumberOrderByCreateDateTimeDesc(String mobileNumber);

    OTP save(OTP otp);

    CustomApiResponse createOTP(EmailOTPSendDto emailOTPSendDto);

    CustomApiResponse verifyOTP(EmailOTPVerifyDto EmailOTPVerifyDto);

}
