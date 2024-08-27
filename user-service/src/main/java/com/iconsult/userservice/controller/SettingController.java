package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.dto.response.SignUpResponse;
import com.iconsult.userservice.service.Impl.SettingServiceImpl;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.validation.Valid;
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
}
