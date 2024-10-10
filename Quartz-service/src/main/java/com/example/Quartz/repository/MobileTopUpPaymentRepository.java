package com.example.Quartz.repository;

import com.example.Quartz.model.entity.ScheduleMobileTopUpPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileTopUpPaymentRepository extends JpaRepository<ScheduleMobileTopUpPayment, Long> {
}
