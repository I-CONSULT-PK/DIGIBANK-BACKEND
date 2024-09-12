package com.iconsult.topup.model.mapper;

import com.iconsult.topup.model.dto.TopUpCustomerDto;
import com.iconsult.topup.model.entity.TopUpCustomer;

@org.mapstruct.Mapper(componentModel = "spring")
public interface TopUpCustomerMapper {

    TopUpCustomer dtoToJpe(TopUpCustomerDto dto);

    TopUpCustomerDto JpeToDto(TopUpCustomer entity);
}

