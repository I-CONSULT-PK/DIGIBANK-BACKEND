package com.iconsult.userservice.controller;

import com.iconsult.userservice.dto.UserActivityRequest;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.entity.UserActivity;
import com.iconsult.userservice.service.Impl.UserActivityImpl;
import com.iconsult.userservice.service.UserActivityService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/userActivity")
@Validated
public class UserActivityController {


    @Autowired
    UserActivityImpl userActivityService;

    @PostMapping("/saveUserActivity")
    public CustomResponseEntity saveUserActivity(@RequestParam String customerId , @RequestParam String activity,
                                                 @RequestParam String pkr){
        return userActivityService.saveUserActivity(customerId,activity, pkr);
    }
    @GetMapping("/getUserActivity")
    public CustomResponseEntity getUserActivity(@RequestParam Long customerId, @RequestParam int days){
        return userActivityService.recordOfUserActivity(customerId, days);
    }
}
