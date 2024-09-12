package com.iconsult.userservice.model.mapper;

import com.iconsult.userservice.model.dto.response.TopPackageTransactionDto;
import com.iconsult.userservice.model.dto.response.TopUpPaymentTransactionDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.Transactions;

public class TopUpPackageTransactionMapper {

    public static Transactions toEntity(TopPackageTransactionDto dto, Account account) {
        Transactions transaction = new Transactions();
        transaction.setAccount(account);
        transaction.setDebitAmt(dto.getDebitAmount());
        transaction.setCreditAmt(dto.getCreditAmount());
        transaction.setCurrentBalance(dto.getCurrentBalance());
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setIbanCode(dto.getIbanCode());
        transaction.setTransactionNarration(dto.getTransactionNarration());

        // Set default or placeholder values for other fields
        transaction.setBankCode("DEFAULT_BANK_CODE");
//        transaction.setCustomer(account.getCustomer()); // Assuming Account has a Customer
        transaction.setReversalAmt(0.0);
        transaction.setRevDate(null);
        transaction.setRevId(null);
        transaction.setRevFlag(null);
        transaction.setPrivateRailId(null);
        transaction.setMerchantId(null);
        transaction.setTopUpFlag(null);
        transaction.setGovtServiceFlag(null);
        transaction.setBeneficiaryId(null);
        transaction.setAdditionalCharges(null);
        transaction.setTaxAmount(0.0);
        transaction.setServiceCharges(0.0);
        transaction.setReceiverAccount(null);
        transaction.setSenderAccount(null);
        transaction.setNatureOfAccount(account.getAccountType()); // Assuming Account has a type
        transaction.setCurrency("DEFAULT_CURRENCY");

        return transaction;
    }

    public static TopPackageTransactionDto toDto(Transactions transaction) {
        TopPackageTransactionDto dto = new TopPackageTransactionDto();
        dto.setAccountNumber(transaction.getAccount().getAccountNumber());
        dto.setDebitAmount(transaction.getDebitAmt());
        dto.setCreditAmount(transaction.getCreditAmt());
        dto.setCurrentBalance(transaction.getCurrentBalance());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setIbanCode(transaction.getIbanCode());
        dto.setTransactionNarration(transaction.getTransactionNarration());

        return dto;
    }
}
