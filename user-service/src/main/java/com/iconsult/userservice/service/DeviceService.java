package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.DeviceDto;
import com.iconsult.userservice.model.dto.request.SettingDTO;
import com.iconsult.userservice.model.dto.request.SignUpDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface DeviceService {


    public CustomResponseEntity signup(SignUpDto signUpDto);

//    public CustomResponseEntity getPinHashByAccountNumberAndPinHash(Long customerId, String devicePin, String uniquePin);

    CustomResponseEntity deviceRegister(Long id, SettingDTO settingDTO);

    CustomResponseEntity loginWithPin(Long customerId, String devicePin, String uniquePin);

    CustomResponseEntity fetchDeviceRegister(SettingDTO settingDTO);

}
