package com.iconsult.userservice.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSmsOtp() {
        return smsOtp;
    }

    public void setSmsOtp(String smsOtp) {
        this.smsOtp = smsOtp;
    }

    public String getEmailOtp() {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp) {
        this.emailOtp = emailOtp;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean verified) {
        isVerified = verified;
    }

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean expired) {
        isExpired = expired;
    }

    public Long getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Long createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Long getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(Long expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }

    public Long getVerifyDateTime() {
        return verifyDateTime;
    }

    public void setVerifyDateTime(Long verifyDateTime) {
        this.verifyDateTime = verifyDateTime;
    }

    public Long getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(Long blockedUntil) {
        this.blockedUntil = blockedUntil;
    }

    public int getInvalidAttemptCount() {
        return invalidAttemptCount;
    }

    public void setInvalidAttemptCount(int invalidAttemptCount) {
        this.invalidAttemptCount = invalidAttemptCount;
    }

    public String getSmsMessage() {
        return smsMessage;
    }

    public void setSmsMessage(String smsMessage) {
        this.smsMessage = smsMessage;
    }

    public String getTxnRefNum() {
        return txnRefNum;
    }

    public void setTxnRefNum(String txnRefNum) {
        this.txnRefNum = txnRefNum;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
