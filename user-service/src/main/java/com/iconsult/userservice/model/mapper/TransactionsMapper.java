package com.iconsult.userservice.model.mapper;

import com.iconsult.userservice.model.dto.request.TransactionsDTO;
import com.iconsult.userservice.model.entity.Transactions;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionsMapper {

    public static TransactionsDTO toDTO(Transactions transaction) {

        TransactionsDTO dto = new TransactionsDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setDebitAmt(transaction.getDebitAmt());
        dto.setCreditAmt(transaction.getCreditAmt());
        dto.setCurrentBalance(transaction.getCurrentBalance());
        dto.setDescription("Money Sent from "+transaction.getSenderAccount()+ " to " + transaction.getReceiverAccount());
        return dto;
    }

    public static List<TransactionsDTO> toDTOList(List<Transactions> transactions) {
        return transactions.stream()
                .map(TransactionsMapper::toDTO)
                .collect(Collectors.toList());
    }
}
