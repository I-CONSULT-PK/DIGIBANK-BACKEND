package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.dto.request.CustomerDto;
import com.iconsult.userservice.model.dto.request.SettingDTO;
import com.iconsult.userservice.model.entity.Device;
import com.iconsult.userservice.service.Impl.SettingServiceImpl;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/settings")
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
    public CustomResponseEntity setTransactionLimit(@RequestParam ("accountNumber") String accountNumber, @RequestParam ("userId") Long userId,@RequestParam ("transactionLimit") Double transactionLimit ) {
        return this.settingService.setTransactionLimit(accountNumber, userId, transactionLimit);

    }

    @PostMapping("/changePassword")
    public CustomResponseEntity changePassword (@RequestParam Long customerId , @RequestParam String oldPassword , @RequestParam String newPassword) throws Exception {
        return this.settingService.changePassword(oldPassword,newPassword,customerId);
    }

    @PostMapping("/updateProfile")
    public CustomResponseEntity updateProfile(CustomerDto customerDto){
        return this.settingService.updateProfile(customerDto);
    }


}
