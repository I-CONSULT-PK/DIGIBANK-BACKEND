package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.FundTransferDto;
import com.iconsult.userservice.model.dto.request.InterBankFundTransferDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface FundTransferService {

    CustomResponseEntity getAllBanks();

    CustomResponseEntity getAccountTitle(String senderAccountNumber);

     CustomResponseEntity fundTransfer(FundTransferDto cbsTransferDto);

    CustomResponseEntity interBankFundTransfer(InterBankFundTransferDto interBankFundTransferDto, String authHeader);
}
