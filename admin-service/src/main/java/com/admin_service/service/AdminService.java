package com.admin_service.service;

import com.admin_service.dto.request.LoginDto;
import com.admin_service.entity.Admin;
import com.admin_service.model.CustomResponseEntity;
import org.springframework.http.ResponseEntity;

public interface AdminService
{
    CustomResponseEntity login(LoginDto loginDto);

    Admin updateAdmin(Admin admin);

}