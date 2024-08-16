package com.iconsult.userservice.controller;

import com.iconsult.userservice.service.FundTransferService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
