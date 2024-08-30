package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.SettingDTO;
import com.iconsult.userservice.model.entity.Device;
import com.iconsult.userservice.model.dto.request.CustomerDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface SettingService {


    CustomResponseEntity setTransactionLimit(String accountNumber, Long userId, Double transferLimit);

    CustomResponseEntity changePassword(String oldPassword,String newPassword, Long id) throws Exception;

    CustomResponseEntity updateProfile(CustomerDto customerDto);
    CustomResponseEntity setDevicePin(Long id, SettingDTO settingDTO);

    CustomResponseEntity<Device> updateDevicePin(String id, SettingDTO settingDTO);
}
