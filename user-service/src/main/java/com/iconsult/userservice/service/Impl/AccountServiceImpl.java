package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.model.dto.request.AccountDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.mapper.AccountMapper;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    AccountMapper accountMapper;

    @Override
    public Account getAccountsByCustomerCnic(String accountNumber) {
        return accountRepository.getAccountByAccountNumber(accountNumber);
    }

    @Override
    public Account createAccount(AccountDto accountDto) {
        Account account = accountMapper.dtoJpe(accountDto);
        return accountRepository.save(account);
    }
}
