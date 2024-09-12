package com.iconsult.topup.service;

import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface SubscribeService {
    public CustomResponseEntity subscribeToPackage(String mobileNumber, Long packageId);
}
