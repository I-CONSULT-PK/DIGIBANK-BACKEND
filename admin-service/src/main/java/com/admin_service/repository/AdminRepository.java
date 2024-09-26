package com.admin_service.repository;

import com.admin_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<User, Long> {

    @Query("SELECT a FROM User a WHERE a.email = ?1 OR a.userName = ?1")
    User findByEmailOrUserName(String identifier);

}
