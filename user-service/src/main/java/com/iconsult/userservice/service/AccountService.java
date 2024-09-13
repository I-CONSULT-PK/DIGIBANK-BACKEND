package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.AccountDto;
import com.iconsult.userservice.model.dto.request.CustomerAccountDto2;
import com.iconsult.userservice.model.dto.response.CbsAccountDto;
import com.iconsult.userservice.model.entity.Account;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface AccountService {


    public Account getAccountsByCustomerCnic(String cnic);

    public Account createAccount(AccountDto accountDto);

    public CustomResponseEntity getAccount(Long CustomerId, String accountNumber);

    public CustomResponseEntity<Account> addAccount(CbsAccountDto accountDto);

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    CustomerAccountDto2 getCustomerAccountDetails(Long customerId, String accountNumber);

}
