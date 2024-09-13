package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

//    @Query("SELECT a FROM Account a WHERE a.customer.cnic = :cnic")
//    List<Account> findAccountsByCustomerCnic(@Param("cnic") String cnic);

    public Account getAccountByAccountNumber(String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber OR a.ibanCode = :iban")
    Account findByAccountNumberOrIban(@Param("accountNumber") String accountNumber, @Param("iban") String iban);



    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber AND a.customer.cnic = :cnic")
    Account findByAccountNumberAndCustomerCnic(@Param("accountNumber") String accountNumber, @Param("cnic") String cnic);
    @Query("SELECT a.customer.id FROM Account a WHERE a.accountNumber = :accountNumber")
    long findCustomerByAccountNumber(@Param("accountNumber") String accountNumber);
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Account findByAccountNumber(@Param("accountNumber") String accountNumber);


    // Custom query to find account based on customer ID and account number
    Account findByCustomerIdAndAccountNumber(Long customerId, String accountNumber);


}
