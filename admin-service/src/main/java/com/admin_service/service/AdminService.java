package com.admin_service.service;

import com.admin_service.dto.request.LoginDto;
import com.admin_service.entity.User;
import com.admin_service.model.CustomResponseEntity;

public interface AdminService
{
    CustomResponseEntity login(LoginDto loginDto);

    User updateAdmin(User user);

}