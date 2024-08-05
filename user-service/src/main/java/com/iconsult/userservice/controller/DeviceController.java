package com.iconsult.userservice.controller;
import com.iconsult.userservice.model.dto.request.DeviceDto;
import com.iconsult.userservice.model.dto.request.SignUpDto;
import com.iconsult.userservice.model.dto.response.SignUpResponse;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.service.Impl.DeviceServiceImpl;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/loginWithPin")
    public CustomResponseEntity login(@Valid  @RequestParam String accountNumber,
                                      @RequestParam String pinHash) {
        return this.deviceService.getPinHashByAccountNumberAndPinHash(accountNumber,pinHash);
    }

}

