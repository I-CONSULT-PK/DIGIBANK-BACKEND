package com.iconsult.userservice.model.mapper;

import com.iconsult.userservice.model.dto.request.CardRequestDto;
import com.iconsult.userservice.model.entity.CardRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardRequestMapper {
    // Method to map CardRequestDto to CardRequest
    CardRequest dtoToEntity(CardRequestDto cardRequestDto);

    // Method to map CardRequest to CardRequestDto
    CardRequestDto entityToDto(CardRequest cardRequest);
}
