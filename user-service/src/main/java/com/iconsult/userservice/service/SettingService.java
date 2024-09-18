package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.SettingDTO;
import com.iconsult.userservice.model.entity.Device;
import com.iconsult.userservice.model.dto.request.CustomerDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface SettingService {


    CustomResponseEntity setTransactionLimit(String accountNumber, Long userId, Double transferLimit);

    CustomResponseEntity changePassword(Long id, String oldPassword,String newPassword) throws Exception;

    CustomResponseEntity updateProfile(CustomerDto customerDto);
    CustomResponseEntity setDevicePin(Long id, SettingDTO settingDTO);

    CustomResponseEntity<Device> updateDevicePin(String id, SettingDTO settingDTO);

    CustomResponseEntity deactivatePin(Long customerId, String devicePin);
}
