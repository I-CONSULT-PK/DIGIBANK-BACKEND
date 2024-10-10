package com.iconsult.userservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MostActiveAccountDto {

    private String userName;            // From Customer
    private String accountType;         // From Account
    private String accountNumber;       // From Account
    private Double lastDebitTransaction; // Last debit transaction (debitAmt)
    private Double lastCreditTransaction; // Last credit transaction (creditAmt)
    private Double totalDebitAmount;    // Sum of all debit transactions
    private Double totalCreditAmount;   // Sum of all credit transactions
    private Long totalTransactions;     // Total number of transactions

}

