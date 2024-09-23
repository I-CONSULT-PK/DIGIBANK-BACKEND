package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.ScheduledTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTransactionsRepository extends JpaRepository<ScheduledTransactions, Long> {
}
