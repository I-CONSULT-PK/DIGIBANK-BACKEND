package com.iconsult.userservice.model.mapper;


import com.iconsult.userservice.model.dto.request.AccountDto;
import com.iconsult.userservice.model.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account dtoJpe(AccountDto accountDto);
}
