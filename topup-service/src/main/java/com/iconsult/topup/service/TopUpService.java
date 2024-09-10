package com.iconsult.topup.service;

import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface TopUpService {

    CustomResponseEntity topUpTransaction (String phoneNumber,
                                           String carrier,
                                           Double amount,
                                           String plan);

}
