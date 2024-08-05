package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.CardRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRequestRepository extends JpaRepository<CardRequest, Long> {
}
