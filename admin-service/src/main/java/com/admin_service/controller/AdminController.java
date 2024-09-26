package com.admin_service.controller;

import com.admin_service.dto.request.LoginDto;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.service.JwtService;

import com.admin_service.service.serviceImpl.AdminServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
