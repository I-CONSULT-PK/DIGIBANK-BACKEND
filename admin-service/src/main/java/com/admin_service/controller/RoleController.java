package com.admin_service.controller;

import com.admin_service.dto.request.AddRoleDto;
import com.admin_service.dto.request.RoleDto;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Controller
@RestController
@RequestMapping("/v1/role")
public class RoleController {
    @Autowired
    RoleService roleService;
    @PostMapping("/addRole")
    public CustomResponseEntity addRole(@RequestBody AddRoleDto addRoleDto)
    {

        return this.roleService.AddRole(addRoleDto);
    }
    @GetMapping("/getAllRoles")
    public CustomResponseEntity getAllRoles(){

        return this.roleService.getAllRoles();
    }
    @DeleteMapping("/deleteRole/{roleId}")
    public CustomResponseEntity deleteRoleById(@PathVariable Long roleId){

        return this.roleService.deleteRoleById(roleId);
    }
    @GetMapping("/getRoleById/{roleId}")
    public CustomResponseEntity getRoleById(@PathVariable Long roleId){

        return this.roleService.getRoleById(roleId);
    }
    @PutMapping(value = "/updateRole")
    public CustomResponseEntity updateRole(@RequestBody RoleDto roleDto) {
        return this.roleService.updateRole(roleDto);
    }
}
