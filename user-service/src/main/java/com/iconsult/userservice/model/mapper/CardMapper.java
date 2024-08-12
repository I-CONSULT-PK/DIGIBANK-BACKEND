package com.iconsult.userservice.model.mapper;

import com.iconsult.userservice.model.dto.response.CardApprovalResDto;
import com.iconsult.userservice.model.entity.Card;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {
    Card dtoJpe(CardApprovalResDto cardDto);
}
