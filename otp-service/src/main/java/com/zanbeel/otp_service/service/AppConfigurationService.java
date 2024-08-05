package com.zanbeel.otp_service.service;

import com.zanbeel.otp_service.config.AppConfiguration;

public interface AppConfigurationService
{
    AppConfiguration findByName(String name);
}
