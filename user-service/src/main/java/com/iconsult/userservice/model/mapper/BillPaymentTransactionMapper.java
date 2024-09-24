package com.iconsult.userservice.model.mapper;

import com.iconsult.userservice.model.dto.response.BillPaymentTransactionDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.Transactions;

import java.text.SimpleDateFormat;


public class BillPaymentTransactionMapper {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public static Transactions toEntity(BillPaymentTransactionDto dto, Account account) {
        Transactions transaction = new Transactions();
        transaction.setAccount(account);
        transaction.setDebitAmt(dto.getDebitAmount());
        transaction.setCreditAmt(dto.getCreditAmount());
        transaction.setCurrentBalance(dto.getCurrentBalance());
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setIbanCode(dto.getIbanCode());
        transaction.setTransactionNarration(dto.getTransactionNarration());
        transaction.setNatureOfAccount(account.getAccountType());
        transaction.setTransactionType("BILL");
        return transaction;
    }

    public static BillPaymentTransactionDto toDto(Transactions transaction) {
        BillPaymentTransactionDto dto = new BillPaymentTransactionDto();
        dto.setAccountNumber(transaction.getAccount().getAccountNumber());
        dto.setDebitAmount(transaction.getDebitAmt());
        dto.setCreditAmount(transaction.getCreditAmt());
        dto.setCurrentBalance(transaction.getCurrentBalance());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setIbanCode(transaction.getIbanCode());
        dto.setTransactionNarration(transaction.getTransactionNarration());
        dto.setTransactionType("BILL");
        return dto;
    }
}

