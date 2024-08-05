package com.zanbeel.otp_service.repository;

import com.zanbeel.otp_service.domain.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OTPRepository extends JpaRepository<OTP, Long> {

    List<OTP> findByMobileNumberAndIsExpired(String mobileNumber, Boolean isExpired);

    OTP findTopByMobileNumberOrderByCreateDateTimeDesc(String mobileNumber);
}
