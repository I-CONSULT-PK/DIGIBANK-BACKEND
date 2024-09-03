package com.iconsult.userservice.controller;

import com.iconsult.userservice.constant.StatementType;
import com.iconsult.userservice.model.dto.request.FundTransferDto;
import com.iconsult.userservice.model.dto.request.InterBankFundTransferDto;
import com.iconsult.userservice.service.FundTransferService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/customer/fund")
public class FundTransferController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FundTransferController.class);

    @Autowired
    private FundTransferService fundTransferService;

    @GetMapping("/getBanks")
    public CustomResponseEntity getAllBanks() {
        return this.fundTransferService.getAllBanks();
    }

    @GetMapping("/getAccountTitle")
    public CustomResponseEntity getAccountTitle(@RequestParam("senderAccountNumber") String senderAccountNumber) {
        return this.fundTransferService.getAccountTitle(senderAccountNumber);
    }

    @PostMapping("/fundTransfer")
    public CustomResponseEntity getAllBanks(@RequestBody FundTransferDto fundTransferDto) {
        return this.fundTransferService.fundTransfer(fundTransferDto);
    }

    @PostMapping("/interBankFundsTransfer")
    public CustomResponseEntity interBankFundsTransfer (@RequestBody InterBankFundTransferDto fundTransferDto,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return this.fundTransferService.interBankFundTransfer(fundTransferDto,authHeader);
    }

    @GetMapping("/generateStatement")
    public CustomResponseEntity<Map<String, Object>> getTransactionsByAccountAndDateRange(
            @RequestParam String accountNumber,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String statementType) {

        return  fundTransferService.generateStatement(accountNumber, startDate, endDate, statementType);

    }

    @PostMapping("/setOneDayLimit")
    public CustomResponseEntity setOneDayLimit(@RequestParam String account ,@RequestParam Long customerId, @RequestParam Double ondDayLimit){
        return fundTransferService.setOneDayLimit(account ,customerId, ondDayLimit);
    }
}
