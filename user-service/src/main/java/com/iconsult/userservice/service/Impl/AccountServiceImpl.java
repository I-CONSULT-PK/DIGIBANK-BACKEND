package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;


    @Override
    public Account getAccountsByCustomerCnic(String accountNumber) {
        return accountRepository.getAccountByAccountNumber(accountNumber);
    }
}
