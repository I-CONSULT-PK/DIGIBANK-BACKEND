package com.iconsult.userservice.controller;

import com.iconsult.userservice.service.Impl.SettingServiceImpl;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    @Autowired
    private SettingServiceImpl settingService;

    @PostMapping("/setDevicePin")
    public CustomResponseEntity setDevicePin(@RequestParam String deviceName, @RequestParam String devicePin) {
        return this.settingService.setDevicePin(deviceName, devicePin);

    }

    @PostMapping("/setTransactionLimit")
    public CustomResponseEntity setTransactionLimit(@RequestParam ("accountNumber") String accountNumber, @RequestParam ("userId") Long userId,@RequestParam ("transactionLimit") Double transactionLimit ) {
        return this.settingService.setTransactionLimit(accountNumber, userId, transactionLimit);

    }
}
