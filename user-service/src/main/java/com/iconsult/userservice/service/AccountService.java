package com.iconsult.userservice.service;
import com.iconsult.userservice.model.dto.request.AccountDto;
import com.iconsult.userservice.model.entity.Account;


import java.util.List;

public interface AccountService {


    public Account getAccountsByCustomerCnic(String cnic);

    public Account createAccount(AccountDto accountDto);

}
