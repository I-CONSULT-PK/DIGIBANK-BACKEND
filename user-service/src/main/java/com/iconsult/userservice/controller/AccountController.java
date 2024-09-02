package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.dto.response.CbsAccountDto;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.service.AccountService;
import com.iconsult.userservice.service.CustomerService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/account")
public class AccountController {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CustomerService customerService;

    //Get Account from Cbs
    @GetMapping("/getAccount")
    public CustomResponseEntity<?> getAccount(@RequestParam("customerId") Long customerId, @RequestParam("accountNumber") String accountNumber) {
        // Fetch the account details from CBS
        CustomResponseEntity accountResponse = accountService.getAccount(customerId, accountNumber);
        return accountResponse;
    }

    //Add Account
    @PostMapping("/addAccount")
    CustomResponseEntity<?> addAccount(@RequestBody CbsAccountDto cbsAccountDto) {
        return accountService.addAccount(cbsAccountDto);
    }
}