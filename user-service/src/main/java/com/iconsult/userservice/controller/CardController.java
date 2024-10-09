package com.iconsult.userservice.controller;

import com.iconsult.userservice.custome.Regex;
import com.iconsult.userservice.model.dto.request.CardDto;
import com.iconsult.userservice.model.dto.request.CardRequestDto;
import com.iconsult.userservice.model.dto.request.ChangePinDto;
import com.iconsult.userservice.service.CardService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/customer/card")
public class CardController {
    @Autowired
    private CardService cardService;
    @Autowired
    Regex regex;


    @PostMapping("/verifyCard")
    public CustomResponseEntity verifyCard(@Valid @RequestBody CardDto cardDto) {
        return this.cardService.cardExist(cardDto);
    }

    @GetMapping("/fetchCardById/{customerId}")
    public CustomResponseEntity fetCardsByCustomerId(@PathVariable("customerId") String customerId) {
        return this.cardService.getAllCardById(customerId);
    }

    @PatchMapping("/updateCardStatus")
    public CustomResponseEntity updateCardStatus(@RequestParam("cardNumber") Long cardNumber, @RequestParam("accountNumber") String accountNumber, @RequestParam("status") Boolean status) {
        return this.cardService.updateCardStatus(cardNumber, accountNumber, status);
    }

    @PostMapping("/cardApprovalRequest")
    public CustomResponseEntity requestApproval(@Valid @RequestBody CardRequestDto cardRequest){
        return this.cardService.createCardRequest(cardRequest);
    }

    @PostMapping("/setPin")
    public CustomResponseEntity setPinDigiBankAndMyDatabase(@RequestParam
        String pin,
        @RequestParam String card){
        return this.cardService.setPinDigiBankAndMyDatabase(pin,card);
    }

    @PostMapping("/sendOtp")
    public CustomResponseEntity sendOtpForChangeCardPin(@RequestParam("customerId") Long customerId) {
        return this.cardService.sendOtpForChangeCardPin(customerId);
    }

    @PostMapping("/changePin")
    public CustomResponseEntity changePin(@RequestBody ChangePinDto changePinRequestDto) {
        return cardService.changePin(changePinRequestDto);
    }

    @GetMapping("/getCardNumbersAgainstAccountNumber")
    public CustomResponseEntity  getCardNumbersAgainstAccountNumber(@RequestParam("accountNumber")  String accountNumber) {
        return this.cardService.getCardNumbersAgainstAccountNumber(accountNumber);
    }
}
