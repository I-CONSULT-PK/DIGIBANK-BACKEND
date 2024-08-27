package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.CardDto;
import com.iconsult.userservice.model.dto.request.CardRequestDto;
import com.iconsult.userservice.model.dto.response.CardResponseDto;
import com.iconsult.userservice.model.dto.response.SignUpResponse;
import com.iconsult.userservice.model.entity.Card;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.util.List;

public interface CardService {
    CustomResponseEntity cardExist(CardDto cardDto);

    CustomResponseEntity getAllCardById(String accountNumber);

    CustomResponseEntity updateCardStatus(Long cardNumber, String accountNumber, Boolean status);
    CustomResponseEntity createCardRequest(CardRequestDto cardRequest);

    CustomResponseEntity setPinDigiBankAndMyDatabase(String pin, String card);
}
