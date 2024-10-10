package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.Transactions;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
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
    @Query("SELECT t FROM Transactions t " +
            "WHERE t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transactions> findByTransactionDate(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);


    //    List<Transactions> findByAccount_IdAndTransactionDateContaining(Long accountId, String transactionDate);
//@Query("SELECT SUM(t.debitAmt) FROM Transactions t WHERE t.account.id = :accountId AND DATE(t.transactionDate) = :date")
//Double sumDebitAmountByAccountAndDate(@Param("accountId") Long accountId, @Param("date") LocalDate date);
List<Transactions> findByAccount_IdAndTransactionTypeAndTransactionDateContaining(Long accountId, String transactionType, String transactionDate);

    // find all transactions for a specific date without the type
    List<Transactions> findByAccount_IdAndTransactionDateContaining(Long accountId, String transactionDate);

    @Query("SELECT SUM(t.debitAmt) FROM Transactions t " +
            "WHERE t.account.id = :accountId " +
            "AND UPPER(t.transactionType) = UPPER(:transactionType) " +
            "AND FUNCTION('DATE', t.startDateTime) = :currentDate")
    Double findTotalTransactionsForType(
            @Param("accountId") Long accountId,
            @Param("transactionType") String transactionType,
            @Param("currentDate") LocalDate currentDate);

    // Dashborad ::

    // Fetch the most recent debit transaction for a given account
    Transactions findTopByAccountAndDebitAmtIsNotNullOrderByTransactionDateDesc(Account account);

    // Fetch the most recent credit transaction for a given account
    Transactions findTopByAccountAndCreditAmtIsNotNullOrderByTransactionDateDesc(Account account);

    // Sum of all debit transactions for a given account
    @Query("SELECT SUM(t.debitAmt) FROM Transactions t WHERE t.account = :account")
    Double calculateTotalDebitAmountByAccount(@Param("account") Account account);

    // Sum of all credit transactions for a given account
    @Query("SELECT SUM(t.creditAmt) FROM Transactions t WHERE t.account = :account")
    Double calculateTotalCreditAmountByAccount(@Param("account") Account account);

    // Count of all transactions for a given account
    @Query("SELECT COUNT(t) FROM Transactions t WHERE t.account = :account")
    Long countTransactionsByAccount(@Param("account") Account account);

}
