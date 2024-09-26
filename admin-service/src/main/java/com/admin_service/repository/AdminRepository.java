package com.admin_service.repository;

import com.admin_service.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("SELECT a FROM Admin a WHERE a.email = ?1 OR a.userName = ?1")
    Admin findByEmailOrUserName(String identifier);

}
