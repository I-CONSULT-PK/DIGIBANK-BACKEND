package com.admin_service.controller;

import com.admin_service.dto.request.LoginDto;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.service.JwtService;

import com.admin_service.service.serviceImpl.AdminServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin")
public class AdminController {
//    @Autowired
//    JwtService jwtService;

    @Autowired
    AdminServiceImpl adminService;

    @PostMapping("/login")
    public CustomResponseEntity login(@Valid @RequestBody LoginDto loginDto)
    {
        return this.adminService.login(loginDto);
    }
    @GetMapping("/list")
    public CustomResponseEntity getUsers(){
        return CustomResponseEntity.error("admin Api");
    }

    @GetMapping("/userList")
    public CustomResponseEntity userList(){
        return CustomResponseEntity.error("user Api");
    }

//    @Autowired
//    AuthenticationManager authenticationManager;
//
//    @PostMapping("/login")
//    public String login(@RequestBody LoginDto loginDto) {
//        // Authenticate user
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginDto.getEmailorUsername(), loginDto.getPassword())
//        );
//        // If authentication is successful, generate a JWT token
//        String token = jwtService.generateToken(authentication.getName());
//
//        // Return token in the response
//        return token;
//    }
}
