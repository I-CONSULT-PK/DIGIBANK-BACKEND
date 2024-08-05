package com.zanbeel.otp_service.service.impl;

import com.zanbeel.otp_service.config.AppConfiguration;
import com.zanbeel.otp_service.repository.AppConfigurationRepository;
import com.zanbeel.otp_service.service.AppConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppConfigurationImpl implements AppConfigurationService

{


//    @Autowired
    private AppConfigurationRepository appConfigurationRepository;
    public AppConfigurationImpl(AppConfigurationRepository appConfigurationRepository) {
        this.appConfigurationRepository = appConfigurationRepository;
    }

    AppConfigurationImpl(){

    }

    @Override
    public AppConfiguration findByName(String name)
    {
        return this.appConfigurationRepository.findByName(name);
    }
}
