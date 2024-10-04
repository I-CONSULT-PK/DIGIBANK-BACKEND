package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.dto.request.ChangePasswordRequest;
import com.iconsult.userservice.model.dto.request.CustomerDto;
import com.iconsult.userservice.model.dto.request.DeactivatePinRequest;
import com.iconsult.userservice.model.dto.request.SettingDTO;
import com.iconsult.userservice.service.Impl.SettingServiceImpl;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/settings")
@Validated
public class SettingController {

    @Autowired
    private SettingServiceImpl settingService;

    @PostMapping("/setDevicePin/{id}")
    public CustomResponseEntity setDevicePin(@PathVariable("id")Long id, @Valid @RequestBody SettingDTO settingDTO) {
        return this.settingService.setDevicePin(id, settingDTO);
    }

    @PutMapping("/updateDevicePin/{id}")
    public CustomResponseEntity updateDevicePin(@PathVariable("id") String id, @RequestBody SettingDTO settingDTO)
    {
        return this.settingService.updateDevicePin(id, settingDTO);
    }

    @PostMapping("/setTransactionLimit")
    public CustomResponseEntity setTransactionLimit(@Valid @RequestParam ("accountNumber")
    String accountNumber,
    @RequestParam ("userId") Long userId,
    @RequestParam  ("transactionLimit") @Min(value = 10000, message = "at least 10,000")
    @Max(value = 1000000, message = "not exceed 1,000,000")
    @Digits(integer = 7, fraction = 0, message = "must be a whole number")
    Double transactionLimit ) {
        return this.settingService.setTransactionLimit(accountNumber, userId, transactionLimit);
    }

    @PutMapping("/setDailyLimit")
    public CustomResponseEntity setDailyLimit(
            @Valid @RequestParam("accountNumber") String accountNumber,
            @RequestParam("customerId") Long customerId,
            @RequestParam("limitValue")
            @Digits(integer = 7, fraction = 0, message = "must be a whole number") Double limitValue,
            @RequestParam("limitType") String limitType) {

        return this.settingService.setDailyLimit(accountNumber, customerId, limitValue, limitType);
    }

/*    @PostMapping("/changePassword")
    public CustomResponseEntity changePassword (@RequestParam("customerId") Long id,
                                                @RequestParam("oldPassword") String oldPassword ,
                                                @RequestParam("newPassword") String newPassword)
            throws Exception {
        return this.settingService.changePassword(id,oldPassword,newPassword);
    }*/

    @PostMapping("/changePassword")
    public CustomResponseEntity changePassword (@RequestParam("customerId") Long id,
                                                @RequestBody ChangePasswordRequest request)
            throws Exception {
        return this.settingService.changePassword(id,request.getOldPassword(),request.getNewPassword());
    }

    @PostMapping("/updateProfile")
    public CustomResponseEntity updateProfile(@RequestBody CustomerDto customerDto){
        return this.settingService.updateProfile(customerDto);
    }

    @PostMapping("/deactivate-pin")
    public CustomResponseEntity deactivatePin(@RequestBody DeactivatePinRequest request) {
        CustomResponseEntity response = settingService.deactivatePin(request.getCustomerId(), request.getUnique());
        return response;
    }


}
