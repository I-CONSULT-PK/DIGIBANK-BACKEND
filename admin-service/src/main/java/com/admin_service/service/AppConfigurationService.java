package com.admin_service.service;

import com.admin_service.entity.AppConfiguration;

public interface AppConfigurationService
{
    AppConfiguration findByName(String name);
}
