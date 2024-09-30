package com.admin_service.controller;

import com.admin_service.model.CustomResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserController {


    @GetMapping("/onlyUserAccess")
    @PreAuthorize("hasRole('USER')")
    public CustomResponseEntity onlyUserAccess(){
        return CustomResponseEntity.error("Only User Access");
    }

    @GetMapping("/all")
    public CustomResponseEntity userListt(){
        return CustomResponseEntity.error("all access users ");
    }
}
