package com.admin_service.repository;

import com.admin_service.entity.HdrAdModule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<HdrAdModule, Long> {

}