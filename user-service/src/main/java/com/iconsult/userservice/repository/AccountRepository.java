package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.dto.request.MostActiveAccountDto;
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


    // For active accounts in Dashboard
    /*@Query("SELECT a FROM Account a JOIN FETCH a.customer c " +
            "JOIN FETCH a.transactions t " +
            "WHERE t.transactionDate = (SELECT MAX(t2.transactionDate) FROM Transactions t2 WHERE t2.account.id = a.id)")
    List<Account> findMostActiveAccounts();*/
//    @Query("SELECT a FROM Account a JOIN FETCH a.customer c " +
//            "LEFT JOIN FETCH c.transactions t " +
//            "WHERE t.transactionDate = (SELECT MAX(t2.transactionDate) FROM Transactions t2 WHERE t2.customer.id = c.id)")
//    List<Account> findMostActiveAccounts();

    /*@Query("SELECT new com.iconsult.userservice.model.dto.request.MostActiveAccountDto(c.userName, a.accountType, a.accountNumber, MAX(t.transactionDate), SUM(COALESCE(t.debitAmt, 0) - COALESCE(t.creditAmt, 0))) " +
            "FROM Account a " +
            "JOIN a.customer c " +
            "JOIN a.transactions t " +
            "GROUP BY c.userName, a.accountType, a.accountNumber " +
            "ORDER BY MAX(t.transactionDate) DESC")
    List<MostActiveAccountDto> findMostActiveAccounts();*/

    /*@Query("SELECT new com.iconsult.userservice.model.dto.request.MostActiveAccountDto(c.userName, a.accountType, a.accountNumber, " +
            "(SELECT COALESCE(t.debitAmt, 0) - COALESCE(t.creditAmt, 0) FROM Transactions t WHERE t.account = a ORDER BY t.transactionDate DESC LIMIT 1), " + // Fetch last transaction amount
            "SUM(COALESCE(t.debitAmt, 0) - COALESCE(t.creditAmt, 0))) " +  // Sum of all transactions
            "FROM Account a " +
            "JOIN a.customer c " +
            "JOIN a.transactions t " +
            "GROUP BY c.userName, a.accountType, a.accountNumber " +
            "ORDER BY MAX(t.transactionDate) DESC")
    List<MostActiveAccountDto> findMostActiveAccounts();*/

/*    @Query("SELECT new com.iconsult.userservice.model.dto.request.MostActiveAccountDto(c.userName AS userName, a.accountType AS accountType, a.accountNumber AS accountNumber, " +
            "(SELECT COALESCE(t2.debitAmt, 0) - COALESCE(t2.creditAmt, 0) FROM Transactions t2 WHERE t2.account = a ORDER BY t2.transactionDate DESC LIMIT 1) AS lastTransactionAmount, " +  // Fetch last transaction amount
            "SUM(COALESCE(t1.debitAmt, 0) - COALESCE(t1.creditAmt, 0)) AS totalAmount) " +  // Sum of all transactions
            "FROM Account a " +
            "JOIN a.customer c " +
            "JOIN a.transactions t1 " +
            "GROUP BY c.userName, a.accountType, a.accountNumber " +
            "ORDER BY MAX(t1.transactionDate) DESC")
    List<MostActiveAccountDto> findMostActiveAccounts();*/

//    @Query("SELECT new com.iconsult.userservice.model.dto.request.MostActiveAccountDto(c.userName AS userName, a.accountType AS accountType, a.accountNumber AS accountNumber, " +
//            "(COALESCE(t2.debitAmt, 0) - COALESCE(t2.creditAmt, 0)) AS lastTransactionAmount, " +  // Fetch last transaction amount directly from the join
//            "SUM(COALESCE(t1.debitAmt, 0) - COALESCE(t1.creditAmt, 0)) AS totalAmount) " +  // Sum of all transactions
//            "FROM Account a " +
//            "JOIN a.customer c " +
//            "JOIN a.transactions t1 " +
//            "JOIN Transactions t2 ON t2.account = a AND t2.transactionDate = (SELECT MAX(t3.transactionDate) FROM Transactions t3 WHERE t3.account = a) " +  // Join the last transaction
//            "GROUP BY c.userName, a.accountType, a.accountNumber, t2.debitAmt, t2.creditAmt " +  // Group by fields including last transaction amounts
//            "ORDER BY MAX(t1.transactionDate) DESC")
//    List<MostActiveAccountDto> findMostActiveAccounts();


    // dashborad
    // Custom method to fetch accounts along with their customer information, if needed
//    @Query("SELECT a FROM Account a JOIN FETCH a.customer")
//    List<Account> findAllWithCustomer();

    List<Account> findAll(); // Fetch all accounts

}
