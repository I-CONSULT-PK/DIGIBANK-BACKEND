package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

//    @Query("SELECT a FROM Account a WHERE a.customer.cnic = :cnic")
//    List<Account> findAccountsByCustomerCnic(@Param("cnic") String cnic);

    public Account getAccountByAccountNumber(String accountNumber);
}
