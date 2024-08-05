package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.CardRequestDto;
import com.iconsult.userservice.model.entity.CardRequest;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CardRequestService {
    public CustomResponseEntity createCardRequest(CardRequestDto cardRequest);
}
