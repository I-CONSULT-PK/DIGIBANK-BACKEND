package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.BillPaymentDto;
import com.iconsult.userservice.model.dto.request.ScheduleBillPaymentRequest;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BillPaymentService  {

    CustomResponseEntity getUtilityDetails(String consumerNumber, String serviceCode, String utilityType , BillPaymentDto billPaymentDto);

    CustomResponseEntity schdeuleUtilityBillPay(ScheduleBillPaymentRequest scheduleBillPaymentRequest);

    CustomResponseEntity getAllBillProviders(String utilityType);

    CustomResponseEntity getUtilityTypes();
}
