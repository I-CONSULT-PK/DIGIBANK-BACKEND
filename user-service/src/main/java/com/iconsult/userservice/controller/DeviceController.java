package com.iconsult.userservice.controller;
import com.iconsult.userservice.model.dto.request.DeviceDto;
import com.iconsult.userservice.model.dto.request.SettingDTO;
import com.iconsult.userservice.model.dto.request.SignUpDto;
import com.iconsult.userservice.model.dto.response.SignUpResponse;
import com.iconsult.userservice.service.Impl.DeviceServiceImpl;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.hibernate.sql.ast.SqlTreeCreationLogger.LOGGER;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceServiceImpl deviceService;

    @PostMapping("/register")
    public CustomResponseEntity registerDevice(@RequestBody SignUpResponse customer) {
        return this.deviceService.registerDevice(customer);

    }

//    @PostMapping("/verify")
//    public ResponseEntity<String> verifyDevice(@RequestBody DeviceDto deviceDto) {
//        boolean verified = deviceService.verifyDevice(deviceDto.getDeviceId(), deviceDto.getVerificationCode());
//        if (verified) {
//            return ResponseEntity.ok("Device verified successfully");
//        } else {
//            return ResponseEntity.status(401).body("Invalid verification code");
//        }
//    }
//
//    @PostMapping("/set-pin")
//    public ResponseEntity<String> setPin(@RequestBody DeviceDto deviceDto) {
//        deviceService.setPin(deviceDto.getDeviceId(), deviceDto.getPin());
//        return ResponseEntity.ok("PIN set successfully");
//    }

    @PostMapping("/signUp")
    public CustomResponseEntity signUp(@Valid @RequestBody SignUpDto signUpDto) {
        return this.deviceService.signup(signUpDto);
    }

//    @PostMapping("/loginWithPin")
//    public CustomResponseEntity login(@Valid  @RequestParam Long customerId,
//                                      @RequestParam String devicePin,  @RequestParam String uniquePin) {
//        return this.deviceService.getPinHashByAccountNumberAndPinHash(customerId,devicePin,uniquePin);
//    }

    @PostMapping("/loginWithPin")
    public CustomResponseEntity loginWithPin(/*@Valid  @RequestParam Long customerId,*/
                                      @RequestParam String devicePin,  @RequestParam String uniquePin) {
        return this.deviceService.loginWithPin(/*customerId,*/devicePin,uniquePin);
    }

    @PostMapping("/deviceRegister/{id}")
    public CustomResponseEntity deviceRegister(@PathVariable("id") Long id, @RequestBody SettingDTO settingDTO) {
        return this.deviceService.deviceRegister(id, settingDTO);
    }

    @GetMapping("/fetchDeviceRegister")
    public CustomResponseEntity fetchDeviceDetailsById(@Valid @RequestParam String customerId){

        if (customerId == null || customerId.trim().isEmpty()) {
            LOGGER.info("Customer ID is required");
            return CustomResponseEntity.error("Customer ID cannot be null or empty");
        }
        return deviceService.fetchDeviceRegister(customerId);
    }
    @DeleteMapping("/delete/{id}")
    public CustomResponseEntity deviceRegister(@PathVariable("id") Long id) {
        return this.deviceService.deleteDevice(id);
    }
}

