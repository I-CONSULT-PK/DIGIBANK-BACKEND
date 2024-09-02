package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.FundTransferDto;
import com.iconsult.userservice.model.dto.request.InterBankFundTransferDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.util.Map;

public interface FundTransferService {

    CustomResponseEntity getAllBanks();

    CustomResponseEntity getAccountTitle(String senderAccountNumber);

    CustomResponseEntity fundTransfer(FundTransferDto cbsTransferDto);

    CustomResponseEntity interBankFundTransfer(InterBankFundTransferDto interBankFundTransferDto, String authHeader);

    CustomResponseEntity<Map<String, Object>>getTransactionsByAccountAndDateRange(
            String accountNumber, String startDate, String endDate);

    CustomResponseEntity<Map<String, Object>> generateMiniStatement(String accountNumber);

    CustomResponseEntity<Map<String, Object>> generateStatement(String accountNumber, String startDate, String endDate, String statementType);
}

