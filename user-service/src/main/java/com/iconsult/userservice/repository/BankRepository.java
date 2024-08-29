package com.iconsult.userservice.repository;


import com.iconsult.userservice.model.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {

    Bank findByBankCode(String bankCode);
}
