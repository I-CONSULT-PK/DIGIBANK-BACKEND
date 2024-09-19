package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Transactions;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {

    @Query("SELECT t FROM Transactions t " +
            "JOIN t.account a " +
            "JOIN a.customer c " +
            "WHERE a.accountNumber = :accountNumber " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transactions> findTransactionsByAccountNumberAndDateRange(
            @Param("accountNumber") String accountNumber,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}


