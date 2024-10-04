package com.example.Quartz.service;


import com.example.Quartz.model.dto.request.ScheduleBillPaymentRequest;
import com.example.Quartz.model.dto.request.ScheduleFundTransferDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.quartz.SchedulerException;


public interface QuartzService {
    CustomResponseEntity scheduleFundTransfer(ScheduleFundTransferDto fundTransferDto, String bearerToken) throws SchedulerException;

    CustomResponseEntity scheduleBillPayment(ScheduleBillPaymentRequest scheduleBillPaymentRequest, String bearerToken) throws SchedulerException;
}
