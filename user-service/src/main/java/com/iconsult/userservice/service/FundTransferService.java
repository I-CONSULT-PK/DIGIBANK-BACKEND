package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.FundTransferDto;
import com.iconsult.userservice.model.dto.request.InterBankFundTransferDto;
import com.iconsult.userservice.model.dto.request.TransactionsDTO;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.util.List;

public interface FundTransferService {

    CustomResponseEntity getAllBanks();

    CustomResponseEntity getAccountTitle(String senderAccountNumber);

    CustomResponseEntity fundTransfer(FundTransferDto cbsTransferDto);

    CustomResponseEntity interBankFundTransfer(InterBankFundTransferDto interBankFundTransferDto, String authHeader);

    CustomResponseEntity<List<TransactionsDTO>> getTransactionsByAccountAndDateRange(
            String accountNumber, String startDate, String endDate);

    CustomResponseEntity<List<TransactionsDTO>> generateMiniStatement(String accountNumber);
}

