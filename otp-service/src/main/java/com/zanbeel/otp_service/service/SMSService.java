package com.zanbeel.otp_service.service;

import com.twilio.sdk.creator.api.v2010.account.MessageCreator;
import com.twilio.sdk.resource.api.v2010.account.Message;
import com.twilio.sdk.type.PhoneNumber;
import com.zanbeel.otp_service.config.TwilioConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SMSService {

    private final TwilioConfig twilioConfig;

    public void sendSms(String mobileNumber, String text, String otpCode){
        PhoneNumber to = new PhoneNumber(mobileNumber);
        PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
        String accSid = twilioConfig.getAccountSid();
        String body = "Dear Customer, we have sent you otp " +otpCode+". Use this passcode for authentication";
        MessageCreator message = Message.create(accSid, to, from, body);
        message.execute();
    }
}
