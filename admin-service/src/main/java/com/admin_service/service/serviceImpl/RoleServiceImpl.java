package com.admin_service.service.serviceImpl;

import com.admin_service.dto.request.AddRoleDto;
import com.admin_service.entity.Role;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.repository.RoleRepository;
import com.admin_service.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Autowired
    RoleRepository roleRepository;
    @Override
    public CustomResponseEntity AddRole(AddRoleDto addRoleDto) {
        if (addRoleDto.getName()==null || addRoleDto.getName().isBlank()) {
            LOGGER.info("data cannot be null");
            return CustomResponseEntity.error("data cannot be null");
        }
        if (roleRepository.existsByName(addRoleDto.getName())) {
            LOGGER.info("Role Already Exist");
            return CustomResponseEntity.error("Role Already Exist");
        }

        Role role = new Role();
        role.setName(addRoleDto.getName());
        roleRepository.save(role);
        return new CustomResponseEntity(roleRepository.save(role),"Role Added Successfully");
    }

    @Override
    public CustomResponseEntity getAllRoles() {
        List<Role> roleList = roleRepository.findAllRolesWithOutSuperAdmin();
        return new CustomResponseEntity(roleList,"");
    }

    @Override
    public CustomResponseEntity deleteRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if(Objects.isNull(role)){

            LOGGER.error("Role Does Not Exist With Id "  + roleId);
            return CustomResponseEntity.error(String.format("Role Does Not Exist With Id "  + roleId));
        }
        roleRepository.delete(role);
        LOGGER.error("Role Deleted Successfully With Id "  + roleId);
        return new CustomResponseEntity(String.format("Role Deleted Successfully With Id "  + roleId));
    }
}
