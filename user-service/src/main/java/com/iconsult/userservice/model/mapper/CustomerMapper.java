package com.iconsult.userservice.model.mapper;

import com.iconsult.userservice.model.dto.request.CustomerDto;
import com.iconsult.userservice.model.dto.request.CustomerSignUpDto;
import com.iconsult.userservice.model.dto.request.SignUpDto;
import com.iconsult.userservice.model.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper
{
    Customer dtoToJpe(CustomerDto customerDto);
    Customer dtoToJpe(CustomerSignUpDto signUpDto);

    CustomerDto jpeToDto(Customer customer);

    Customer dtoToJpeSignUp(SignUpDto signUpDto);
}