package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.CardDto;
import com.iconsult.userservice.model.dto.request.CardRequestDto;
import com.iconsult.userservice.model.dto.request.ChangePinDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface CardService {
    CustomResponseEntity cardExist(CardDto cardDto);

    CustomResponseEntity getAllCardById(String accountNumber);

    CustomResponseEntity updateCardStatus(Long cardNumber, String accountNumber, Boolean status);
    CustomResponseEntity createCardRequest(CardRequestDto cardRequest);

    CustomResponseEntity setPinDigiBankAndMyDatabase(String pin, String card);

    CustomResponseEntity sendOtpForChangeCardPin(Long customerId);
    CustomResponseEntity changePin(ChangePinDto changePinRequestDto);

    CustomResponseEntity getCardNumbersAgainstAccountNumber(String accountNumber);
}
