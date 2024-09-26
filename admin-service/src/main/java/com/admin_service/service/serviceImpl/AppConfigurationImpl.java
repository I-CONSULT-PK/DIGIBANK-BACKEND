package com.admin_service.service.serviceImpl;

import com.admin_service.entity.AppConfiguration;
import com.admin_service.repository.AppConfigurationRepository;
import com.admin_service.service.AppConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppConfigurationImpl implements AppConfigurationService
{
    @Autowired
    private AppConfigurationRepository appConfigurationRepository;

    @Override
    public AppConfiguration findByName(String name) {
        return this.appConfigurationRepository.findByName(name);
    }

}
