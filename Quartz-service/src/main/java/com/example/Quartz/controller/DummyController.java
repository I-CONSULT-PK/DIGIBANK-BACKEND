package com.example.Quartz.controller;


import com.example.Quartz.model.dto.request.ScheduleBillPaymentRequest;
import com.example.Quartz.model.dto.request.ScheduleFundTransferDto;
import com.example.Quartz.model.dto.request.ScheduleMobileTopUpPaymentRequest;
import com.example.Quartz.service.MobileTopUpQuartzService;
import com.example.Quartz.model.dto.request.ScheduleIbftFundTransferDto;
import com.example.Quartz.service.QuartzService;
import com.zanbeel.customUtility.exception.GlobalExceptionHandler;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.validation.Valid;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/transfer")
public class DummyController extends GlobalExceptionHandler {

    @Autowired
    QuartzService quartzService;

    @Autowired
    MobileTopUpQuartzService mobileTopUpQuartzService;

    @PostMapping("/scheduleFundTransfer")
    public CustomResponseEntity getAllBanks(@Valid @RequestBody ScheduleFundTransferDto fundTransferDto,  @RequestHeader("Authorization") String bearerToken) throws SchedulerException {
        return this.quartzService.scheduleFundTransfer(fundTransferDto,bearerToken);
    }


    @PostMapping("/billPaymentTransfer")
    public CustomResponseEntity billPaymentTrasnfer(@Valid @RequestBody ScheduleBillPaymentRequest scheduleBillPaymentRequest, @RequestHeader("Authorization") String bearerToken) throws SchedulerException {
        return this.quartzService.scheduleBillPayment(scheduleBillPaymentRequest,bearerToken);
    }

    @PostMapping("/scheduleIbftFundTransfer")
    public CustomResponseEntity scheduleIbftFundTransfer(@Valid @RequestBody ScheduleIbftFundTransferDto fundTransferDto, @RequestHeader("Authorization") String bearerToken) throws SchedulerException {
        return this.quartzService.scheduleIbftFundTransfer(fundTransferDto,bearerToken);
    }

    @PostMapping("/schedulemobileTop")
    public CustomResponseEntity scheduleMobileTop(@Valid @RequestBody ScheduleMobileTopUpPaymentRequest scheduleMobileTopUpPaymentRequest, @RequestHeader("Authorization") String bearerToken)
    {
        return this.mobileTopUpQuartzService.scheduleMobileTopUp(scheduleMobileTopUpPaymentRequest,bearerToken);
    }
}
