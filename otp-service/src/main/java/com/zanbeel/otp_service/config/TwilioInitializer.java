package com.zanbeel.otp_service.config;

import com.twilio.sdk.Twilio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioInitializer {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwilioInitializer.class);

    @Autowired
    private final TwilioConfig twilioConfig;

    @Autowired
    public TwilioInitializer(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getAccountSid(),twilioConfig.getAuthToken());
        LOGGER.info("Twilio initialized with account sid {}",twilioConfig.getAccountSid());
    }
}
