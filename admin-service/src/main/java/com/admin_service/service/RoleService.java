package com.admin_service.service;

import com.admin_service.dto.request.AddRoleDto;
import com.admin_service.dto.request.RoleDto;
import com.admin_service.model.CustomResponseEntity;

public interface RoleService {
    CustomResponseEntity AddRole(AddRoleDto addRoleDto);

    CustomResponseEntity getAllRoles();

    CustomResponseEntity deleteRoleById(Long roleId);

    CustomResponseEntity getRoleById(Long roleId);

    CustomResponseEntity updateRole(RoleDto roleDto);
}
