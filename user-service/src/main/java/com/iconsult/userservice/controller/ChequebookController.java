package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.dto.response.ChequebookDto;
import com.iconsult.userservice.service.ChequebookService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/chequebooks")
public class ChequebookController {

    @Autowired
    private ChequebookService chequebookService;

    //Create Chequebook request with account number
    @PostMapping("/request")
    public CustomResponseEntity<ChequebookDto> createChequebookRequest(@RequestBody ChequebookDto chequebookDto, @RequestParam("accountNumber") String accountNumber) {
        chequebookDto.setAccountNumber(accountNumber);
        return chequebookService.createChequebookRequest(chequebookDto, accountNumber);
    }

    //Cancel Chequebook request with account number
    @PostMapping("/cancel")
    public CustomResponseEntity<?> cancelChequebookRequest(@RequestParam("accountNumber") String accountNumber) {
        CustomResponseEntity<String> response = chequebookService.cancelChequebookRequest(accountNumber);
        if (response.isSuccess()) {
            return new CustomResponseEntity<>(response.getMessage());
        } else {
            return CustomResponseEntity.error(response.getMessage());
        }
    }

    // Get chequebook by ID
    @GetMapping("/getChequebook/{id}")
    public CustomResponseEntity<ChequebookDto> getChequebookById(@PathVariable Long id) {
        return chequebookService.getChequebookById(id);
    }

    // Get all chequebooks
    @GetMapping("/getAllChequebooks")
    public CustomResponseEntity<List<ChequebookDto>> getAllChequebooks() {
        return chequebookService.getAllChequebooks();
    }

    // Update chequebook by id
    @PutMapping("/updateChequebook/{id}")
    public CustomResponseEntity<ChequebookDto> updateChequebookRequest(
            @RequestBody ChequebookDto chequebookDto,
            @PathVariable Long id) {
        return chequebookService.updateChequebookRequest(chequebookDto, id);
    }

    // Delete chequebook by ID
    @DeleteMapping("/deleteChequebook/{id}")
    public CustomResponseEntity<ChequebookDto> deleteChequebook(@PathVariable Long id) {
        return chequebookService.deleteChequebook(id);
    }
}
