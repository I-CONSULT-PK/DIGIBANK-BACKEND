package com.iconsult.userservice.controller;

import com.iconsult.userservice.dto.UserActivityRequest;
import com.iconsult.userservice.model.entity.UserActivity;
import com.iconsult.userservice.service.Impl.UserActivityImpl;
import com.iconsult.userservice.service.UserActivityService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/userActivity")
@Validated
public class UserActivityController {


    @Autowired
    UserActivityImpl userActivityService;

    @PostMapping("/saveUserActivity")
    public CustomResponseEntity saveUserActivity(@RequestParam String customerId , @RequestParam String activity){
        return userActivityService.saveUserActivity(customerId,activity);
    }
}
