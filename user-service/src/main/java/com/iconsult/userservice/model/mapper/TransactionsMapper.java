package com.iconsult.userservice.model.mapper;

import com.iconsult.userservice.model.dto.request.TransactionsDTO;
import com.iconsult.userservice.model.entity.Transactions;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionsMapper {

    public static TransactionsDTO toDTO(Transactions transaction) {
        TransactionsDTO dto = new TransactionsDTO();
        dto.setId(transaction.getId());
        dto.setBankCode(transaction.getBankCode());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setDebitAmt(transaction.getDebitAmt());
        dto.setCreditAmt(transaction.getCreditAmt());
        dto.setCurrentBalance(transaction.getCurrentBalance());
        dto.setTransactionNarration(transaction.getTransactionNarration());
        dto.setAccountNumber(transaction.getAccount().getAccountNumber());
        dto.setCustomerFirstName(transaction.getAccount().getCustomer().getFirstName());
        dto.setCustomerLastName(transaction.getAccount().getCustomer().getLastName());
        dto.setCustomerEmail(transaction.getAccount().getCustomer().getEmail());
        dto.setCustomerAddress(transaction.getAccount().getCustomer().getRegisteredAddress());
        dto.setMobileNumber(transaction.getAccount().getCustomer().getMobileNumber());
        dto.setAccountOpeningDate(String.valueOf(transaction.getAccount().getAccountOpenDate()));
        dto.setBalance(String.valueOf(transaction.getCurrentBalance()));
        dto.setDescription("Money Sent from "+transaction.getSenderAccount()+ " to " + transaction.getReceiverAccount());
        dto.setNatureOfAccount(transaction.getAccount().getAccountType());
        dto.setCurrency(transaction.getCurrency());
        dto.setIbanCode(transaction.getAccount().getIbanCode());


        return dto;
    }

    public static List<TransactionsDTO> toDTOList(List<Transactions> transactions) {
        return transactions.stream()
                .map(TransactionsMapper::toDTO)
                .collect(Collectors.toList());
    }
}
