package com.iconsult.userservice.service;

import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface SettingService {
    CustomResponseEntity setDevicePin(String deviceName, String devicePin);

    CustomResponseEntity setTransactionLimit(String accountNumber, Long userId, Double transferLimit);
}
