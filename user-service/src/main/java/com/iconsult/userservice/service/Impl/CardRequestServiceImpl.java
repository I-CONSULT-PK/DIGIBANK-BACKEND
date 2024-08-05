package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.model.dto.request.CardRequestDto;
import com.iconsult.userservice.model.entity.CardRequest;
import com.iconsult.userservice.model.mapper.CardRequestMapper;
import com.iconsult.userservice.repository.CardRequestRepository;
import com.iconsult.userservice.service.CardRequestService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardRequestServiceImpl implements CardRequestService {
    @Autowired
    private CardRequestRepository requestRepository;
    @Autowired
    private CardRequestMapper cardRequestMapper;

    private CustomResponseEntity response;
    @Override
    public CustomResponseEntity createCardRequest(CardRequestDto cardRequestDto) {
        CardRequest cardRequest = cardRequestMapper.dtoToEntity(cardRequestDto);
        cardRequest.setRequestStatus("pending");
        cardRequest = requestRepository.save(cardRequest);

        CustomResponseEntity customResponseEntity = new CustomResponseEntity();
        customResponseEntity.setData(cardRequest);
        return customResponseEntity;
    }
    public CardRequest convertToEntity(CardRequestDto cardRequestDto) {
        return cardRequestMapper.dtoToEntity(cardRequestDto);
    }

    public CardRequestDto convertToDto(CardRequest cardRequest) {
        return cardRequestMapper.entityToDto(cardRequest);
    }
}
