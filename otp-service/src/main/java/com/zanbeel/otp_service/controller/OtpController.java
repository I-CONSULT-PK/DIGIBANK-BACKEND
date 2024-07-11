package com.zanbeel.otp_service.controller;

import com.zanbeel.otp_service.config.CustomApiResponse;
import com.zanbeel.otp_service.dto.EmailOTPSendDto;
import com.zanbeel.otp_service.dto.EmailOTPVerifyDto;
import com.zanbeel.otp_service.service.impl.OTPServiceImpl;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/otp")
public class OtpController {

    OTPServiceImpl service;

    public OtpController(OTPServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/ping")
    public String ping() {
        return "OTP Service is Up and Running!";
    }

    @PostMapping("/createOTP")
    public CustomApiResponse createOTP(@Valid @RequestBody EmailOTPSendDto OTPDto){
        CustomApiResponse response = this.service.createOTP(OTPDto);
        return response;
    }

    @PostMapping("/verifyOTP")
    public CustomApiResponse verifyOTP(@Valid @RequestBody EmailOTPVerifyDto OTPDto){
        return this.service.verifyOTP(OTPDto);
    }
}
