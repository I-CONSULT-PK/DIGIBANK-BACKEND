package com.admin_service.repository;

import com.admin_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String username);
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.sessionToken = :token AND u.sessionTokenExpireTime > :currentTime")
    boolean isValidToken(@Param("token") String token, @Param("currentTime") long currentTime);

    Optional<User> findBySessionToken(String token);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);
}

