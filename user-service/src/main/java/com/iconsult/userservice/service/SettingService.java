package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.CustomerDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface SettingService {
    CustomResponseEntity setDevicePin(String deviceName, String devicePin);

    CustomResponseEntity setTransactionLimit(String accountNumber, Long userId, Double transferLimit);

    CustomResponseEntity changePassword(String oldPassword,String newPassword, Long id) throws Exception;

    CustomResponseEntity updateProfile(CustomerDto customerDto);
}
