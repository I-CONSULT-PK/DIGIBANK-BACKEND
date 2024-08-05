package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.ImageVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageVerificationRepository extends JpaRepository<ImageVerification, Long> {
}
