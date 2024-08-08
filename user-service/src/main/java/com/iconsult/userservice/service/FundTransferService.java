package com.iconsult.userservice.service;

import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface FundTransferService {

    CustomResponseEntity getAllBanks();

    CustomResponseEntity getAccountTitle(String senderAccountNumber);
}
