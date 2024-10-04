package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.ScheduleBillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchdeuledBillPaymentRepository extends JpaRepository<ScheduleBillPayment, Long> {
}
