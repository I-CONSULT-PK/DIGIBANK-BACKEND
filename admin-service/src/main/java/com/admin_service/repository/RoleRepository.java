package com.admin_service.repository;

import com.admin_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    boolean existsByName(String name);

    @Query("SELECT r FROM Role r WHERE r.name <> 'ROLE_SUPER_ADMIN'")
    List<Role> findAllRolesWithOutSuperAdmin();
}
