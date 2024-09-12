package com.iconsult.userservice.controller;

import com.iconsult.userservice.constant.StatementType;
import com.iconsult.userservice.custome.Regex;
import com.iconsult.userservice.model.dto.request.FundTransferDto;
import com.iconsult.userservice.model.dto.request.InterBankFundTransferDto;
import com.iconsult.userservice.service.FundTransferService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/customer/fund")
@Validated
public class FundTransferController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FundTransferController.class);
    @Autowired Regex regex;
    @Autowired
    private FundTransferService fundTransferService;

    @GetMapping("/getBanks")
    public CustomResponseEntity getAllBanks() {
        return this.fundTransferService.getAllBanks();
    }

    @GetMapping("/getAccountTitle")
    public CustomResponseEntity getAccountTitle(@RequestParam("senderAccountNumber")
        @Pattern(regexp = "^zanbeel-\\w+$", message = "Account must be in the format 'zanbeel-xxxx', where xxxx is alphanumeric.")
        String senderAccountNumber) {
        return this.fundTransferService.getAccountTitle(senderAccountNumber);
    }

    @PostMapping("/fundTransfer")
    public CustomResponseEntity getAllBanks(@Valid @RequestBody FundTransferDto fundTransferDto) {
        return this.fundTransferService.fundTransfer(fundTransferDto);
    }

    @PostMapping("/interBankFundsTransfer")
    public CustomResponseEntity interBankFundsTransfer (@RequestBody InterBankFundTransferDto fundTransferDto) {
        return this.fundTransferService.interBankFundTransfer(fundTransferDto);
    }

    @GetMapping("/generateStatement")
    public CustomResponseEntity<Map<String, Object>> getTransactionsByAccountAndDateRange(
            @RequestParam
            String accountNumber,
            @RequestParam
            String startDate  ,
            @RequestParam
            String endDate,
            @RequestParam String statementType
            ){
        CustomResponseEntity accountNumberFormat = regex.checkAccountNumberFormat(accountNumber);
        if (!accountNumberFormat.isSuccess()) {
            return accountNumberFormat;
        }
        return  fundTransferService.generateStatement(accountNumber, startDate, endDate, statementType);
    }

    @PostMapping("/setOneDayLimit")
    public CustomResponseEntity setOneDayLimit(@RequestParam
        String account ,@RequestParam Long customerId, @RequestParam Double ondDayLimit){
        return fundTransferService.setOneDayLimit(account ,customerId, ondDayLimit);
    }
}
