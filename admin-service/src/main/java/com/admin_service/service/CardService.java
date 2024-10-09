package com.admin_service.service;

import com.admin_service.model.CustomResponseEntity;

public interface CardService {

    CustomResponseEntity getCardNumbersAgainstAccountNumber(String accountNumber);
}
