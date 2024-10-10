package com.example.Quartz.service;

import com.example.Quartz.model.dto.request.ScheduleMobileTopUpPaymentRequest;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface MobileTopUpQuartzService {

    CustomResponseEntity scheduleMobileTopUp(ScheduleMobileTopUpPaymentRequest scheduleMobileTopUpPaymentRequest,String bearerToken);

}
