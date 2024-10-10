package com.iconsult.userservice.repository;


import com.iconsult.userservice.model.entity.ScheduleMobileTopUpPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleMobileTopUpPaymentRepository extends JpaRepository<ScheduleMobileTopUpPayment, Long> {
}
