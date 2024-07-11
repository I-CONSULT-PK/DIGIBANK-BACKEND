package com.zanbeel.otp_service.service.impl;

import com.zanbeel.otp_service.config.CustomApiResponse;
import com.zanbeel.otp_service.constants.DeliveryPreference;
import com.zanbeel.otp_service.constants.Error;
import com.zanbeel.otp_service.domain.OTP;
import com.zanbeel.otp_service.dto.EmailDto;
import com.zanbeel.otp_service.dto.EmailOTPSendDto;
import com.zanbeel.otp_service.dto.EmailOTPVerifyDto;
import com.zanbeel.otp_service.exception.OtpException;
import com.zanbeel.otp_service.repository.OTPRepository;
import com.zanbeel.otp_service.service.EmailService;
import com.zanbeel.otp_service.service.OTPService;
import com.zanbeel.otp_service.service.SMSService;
import com.zanbeel.otp_service.util.Util;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OTPServiceImpl implements OTPService {

    public OTPServiceImpl(){

    }

    private final Logger LOGGER = LoggerFactory.getLogger(OTPServiceImpl.class);

    private static final Integer BLOCK_DURATION = 2;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

    @Autowired
    private AppConfigurationImpl appConfigurationImpl;

    private CustomApiResponse response;

    @Autowired
    private OTPRepository otpRepository;




    String deliveryPreference = DeliveryPreference.EMAIL.getValue();


    public OTPServiceImpl(EmailService emailService) {

        this.emailService = emailService;
    }


    @Override
    public CustomApiResponse createOTP(EmailOTPSendDto OTPDto) {
        LOGGER.info("Executing createOTP Request...");

        List<OTP> otps = findByMobileNumberAndIsExpired(OTPDto.getMobileNumber(), false);

        if(!otps.isEmpty()){

            for(OTP otp : otps){
                otp.setIsExpired(true);
                otp.setIsVerified(false);
            }
            otpRepository.saveAll(otps);
        }

        OTP existingOtp = findTopByMobileNumberOrderByCreateDateTimeDesc(OTPDto.getMobileNumber());
        if (existingOtp != null) {
            validateAccountBlocked(existingOtp);
        }

        if (!createAndSendOTP(OTPDto)) {
            LOGGER.error("Failed to create & Send OTP for Mobile [" + OTPDto.getMobileNumber() + "], rejecting...");
            throw new ServiceException("SMS Gateway Down");
        }

        return new CustomApiResponse("OTP has been sent to your registered email '"+OTPDto.getEmail()+"'");
    }

/*    private void expireExistingOTPs(String mobileNumber) {
        for (OTP otp : findByMobileNumberAndIsExpired(mobileNumber, false)) {
            otp.setIsExpired(true);
            otp.setIsVerified(false);
            save(otp);
        }
    }*/

    public Boolean createAndSendOTP(EmailOTPSendDto emailOTPSendDto) {
        LOGGER.info("Generating OTP...");


        // String otpCode = Util.generateOTP(5); // Generating OTP of length 5
        String emailOtp = Util.generateOTP(5); //when deliveryPreference is set to both
        String smsOtp = Util.generateOTP(5); //when deliveryPreference is set to both
        LOGGER.info("OTP Generated...");

        OTP otp = new OTP();
        otp.setMobileNumber(emailOTPSendDto.getMobileNumber());
        otp.setEmail(emailOTPSendDto.getEmail());
        otp.setIsExpired(false);
        otp.setIsVerified(false);
        otp.setCreateDateTime(Long.parseLong(Util.dateFormat.format(new Date())));
        otp.setReason(emailOTPSendDto.getReason());
        //AppConfiguration appConfiguration = this.appConfigurationImpl.findByName("OTP_EXPIRE_TIME");
        otp.setExpiryDateTime(System.currentTimeMillis() + 60000);
        //otp.setExpiryDateTime(Long.parseLong(OtpGenerator.dateFormat.format(DateUtil.addMinutes(new Date(), Integer.parseInt(appConfiguration.getValue())))));


        // Sending email with OTP
        try {
            if(deliveryPreference.equals(DeliveryPreference.EMAIL.getValue())){
                otp.setSmsMessage("Dear Customer, your OTP to complete your request is " + emailOtp);
                otp.setEmailOtp(emailOtp);
                sendOtpEmail(emailOTPSendDto.getEmail(), emailOTPSendDto.getReason(), emailOtp);
                LOGGER.info("Email sent successfully to [{}]", emailOTPSendDto.getEmail());

                // Save OTP log
                if (save(otp).getId() != null) {
                    LOGGER.info("OTP has been saved with Id: {}", otp.getId());
                    return true;
                }
            }
            if(deliveryPreference==DeliveryPreference.SMS.getValue()){
                otp.setSmsOtp(smsOtp);
                otp.setSmsMessage("Dear Customer, your OTP to complete your request is " + smsOtp);
              //  sendOtpSms(OTPDto.getMobileNumber(), OTPDto.getReason(), smsOtp);
                LOGGER.info("Sms sent successfully to [{}]", emailOTPSendDto.getMobileNumber());

                // Save OTP log
                if (save(otp).getId() != null) {
                    LOGGER.info("OTP has been saved with Id: {}", otp.getId());
                    return true;
                }
            }

            if(deliveryPreference==DeliveryPreference.BOTH.getValue()){
                otp.setSmsOtp(smsOtp);
                otp.setEmailOtp(emailOtp);
                otp.setSmsMessage("Dear Customer, your OTPs to complete your request are : email " + emailOtp+" sms "+smsOtp);
                sendOtpEmail(emailOTPSendDto.getEmail(), "FUNDS-TRANSFER", emailOtp);
              //  sendOtpSms(OTPDto.getMobileNumber(), OTPDto.getReason(), smsOtp);
                LOGGER.info("Email sent successfully to [{}]", emailOTPSendDto.getEmail());
                LOGGER.info("Sms sent successfully to [{}]", emailOTPSendDto.getMobileNumber());

                // Save OTP log
                if (save(otp).getId() != null) {
                    LOGGER.info("OTP has been saved with Id: {}", otp.getId());
                    return true;
                }
            }

        } catch (Exception e) {
            LOGGER.error("Failed to send email to [{}]: {}", emailOTPSendDto.getEmail(), e.getMessage());
            LOGGER.info("Failed to send sms to [{}]", emailOTPSendDto.getMobileNumber());
        }
        return false;
    }

    private void sendOtpEmail(String email, String reason, String otpCode) {
        EmailDto dto = new EmailDto();
        dto.setRecipient(email);
        dto.setSubject("DigiBank");
        dto.setBody("Your OTP for " + reason + " is : " + otpCode);
        emailService.sendSimpleMessage(dto);
    }

    private void sendOtpSms(String mobileNumber, String text, String otpCode) {
        smsService.sendSms(mobileNumber, " ", otpCode);
    }

    private void sendNotificationEmail(String email, String message) {
        EmailDto dto = new EmailDto();
        dto.setRecipient(email);
        dto.setSubject("DigiBank");
        dto.setBody(message);
        emailService.sendSimpleMessage(dto);
    }

    @Override
    public CustomApiResponse verifyOTP(EmailOTPVerifyDto EmailOTPVerifyDto) {
        LOGGER.info("Executing confirmOTP Request...");


        List<OTP> otpList = findByMobileNumberAndIsExpired(EmailOTPVerifyDto.getMobileNumber(), false);

        Collections.sort(otpList, Comparator.comparingLong(OTP::getExpiryDateTime).reversed());

        long currentTime = System.currentTimeMillis();

        if (otpList == null || otpList.isEmpty()) {
            return new CustomApiResponse(1013, "OTP not found");
        }

        for(OTP otp : otpList){
            if(otp != null && otp.getExpiryDateTime() < currentTime && !otp.getIsVerified()){
                LOGGER.info("OTP has expired for customer [{}], replying...", EmailOTPVerifyDto.getEmail());
                otp.setIsExpired(true);
                otp.setIsVerified(false);
                save(otp);
                return new CustomApiResponse(1013, "OTP has expired");
            }
            if(otp != null && isValidOTP(otp, EmailOTPVerifyDto)){
                updateOtpStatus(otp, true, 0);
                LOGGER.info("OTP verified successfully for customer [{}], replying...", EmailOTPVerifyDto.getEmail());
                return new CustomApiResponse("verified otp successfully");
            }

            if(otp !=null){
            otp.setInvalidAttemptCount(otp.getInvalidAttemptCount()+1);
            if (otp.getInvalidAttemptCount() > 3) {
                otp.setIsExpired(true);
                otp.setBlockedUntil(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(BLOCK_DURATION));
                save(otp);
                sendNotificationEmail(EmailOTPVerifyDto.getEmail(), "Your account is temporarily blocked due to multiple incorrect OTP attempts. " +
                        "If this wasn't you, please call +923333739201.");
                return new CustomApiResponse("Mobile number blocked!");
            } else {
                save(otp);
                sendNotificationEmail(EmailOTPVerifyDto.getEmail(), "You have made an incorrect OTP attempt. " +
                        "If this wasn't you, please call +923333739201.");
                return new CustomApiResponse("Invalid OTP");
            } }

        }

        return new CustomApiResponse(1013, "OTP not found");
    }
    private boolean isValidOTP(OTP otp, EmailOTPVerifyDto EmailOTPVerifyDto) {
//        if (OTPDto.getDeliveryPreference() == DeliveryPreference.EMAIL) {
        if (deliveryPreference.equals(DeliveryPreference.EMAIL.getValue())) {
            return otp.getEmailOtp().equals(EmailOTPVerifyDto.getEmailOtp()) && !otp.getIsVerified();
        }
        if (deliveryPreference == DeliveryPreference.SMS.getValue()) {
            return otp.getSmsOtp().equals(EmailOTPVerifyDto.getSmsOtp()) && !otp.getIsVerified();
        }
        if (deliveryPreference == DeliveryPreference.BOTH.getValue()) {
            return otp.getEmailOtp().equals(EmailOTPVerifyDto.getEmailOtp()) &&
                    otp.getSmsOtp().equals(EmailOTPVerifyDto.getSmsOtp()) &&
                    !otp.getIsVerified();
        }
        return false;
    }

    private void updateOtpStatus(OTP otp, boolean isVerified, int invalidAttemptCount) {
        otp.setIsExpired(true);
        otp.setIsVerified(isVerified);
        otp.setInvalidAttemptCount(invalidAttemptCount);
        otp.setVerifyDateTime(System.currentTimeMillis());
        save(otp);
    }

    private void validateAccountBlocked(OTP otp) {
        if (otp.getBlockedUntil() != null && otp.getBlockedUntil() > System.currentTimeMillis()) {
            throw new OtpException(Error.ACCOUNT_BLOCKED);
        }
    }

    @Override
    public List<OTP> findByMobileNumberAndIsExpired(String mobileNumber, Boolean isExpired) {
        return this.otpRepository.findByMobileNumberAndIsExpired(mobileNumber, isExpired);
    }

    @Override
    public OTP findTopByMobileNumberOrderByCreateDateTimeDesc(String mobileNumber) {
        return this.otpRepository.findTopByMobileNumberOrderByCreateDateTimeDesc(mobileNumber);
    }


    @Override
    public OTP save(OTP otp) {
        return this.otpRepository.save(otp);
    }

}
