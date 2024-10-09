package com.admin_service.controller;


import com.admin_service.model.CustomResponseEntity;
import com.admin_service.service.serviceImpl.CardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/card")
public class CardController {


    @Autowired
    private CardServiceImpl cardService;


    @GetMapping("/getCardNumbersAgainstAccountNumber")
    public CustomResponseEntity getCardNumbersAgainstAccountNumber(@RequestParam("accountNumber")String accountNumber) {
        return this.cardService.getCardNumbersAgainstAccountNumber(accountNumber);
    }


}
