package com.iconsult.userservice.service;

import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface TopUpService {

    CustomResponseEntity getMobileNumberAndPlanDetail(String phoneNumber,Double amount,String carrier,String plan , String accountNumber);

    CustomResponseEntity getAllNetworkPackages();

    CustomResponseEntity packageTransaction(Long packageId, String accountNumber, String mobileNumber);
}
