package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.SignUpDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface DeviceService {


    public CustomResponseEntity signup(SignUpDto signUpDto);

    public CustomResponseEntity getPinHashByAccountNumberAndPinHash(String accountNumber, String pinHash);
}
