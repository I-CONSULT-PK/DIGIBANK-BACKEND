package com.zanbeel.otp_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mobileNumber;
    private String email;
    private String smsOtp;
    private String emailOtp;
    private Boolean isVerified;
    private Boolean isExpired;
    private Long createDateTime;
    private Long expiryDateTime;
    private Long verifyDateTime;
    private Long blockedUntil;
    private int invalidAttemptCount;
    private String smsMessage;
    private String txnRefNum;
    private String reason;
}
