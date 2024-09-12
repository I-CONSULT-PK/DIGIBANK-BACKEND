package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.dto.response.ChequebookDto;
import com.iconsult.userservice.service.ChequebookService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chequebooks")
public class ChequebookController {

    @Autowired
    private ChequebookService chequebookService;

    @PostMapping("/request")
    public CustomResponseEntity<ChequebookDto> createChequebookRequest(@RequestBody ChequebookDto chequebookDto,@RequestParam("accountNumber")String accountNumber) {
        chequebookDto.setAccountNumber(accountNumber);
        return chequebookService.createChequebookRequest(chequebookDto,accountNumber);
    }

    @PostMapping("/cancel")
    public CustomResponseEntity<?> cancelChequebookRequest(@RequestParam("accountNumber") String accountNumber) {
        CustomResponseEntity<String> response = chequebookService.cancelChequebookRequest(accountNumber);
        if (response.isSuccess()) {
            return new CustomResponseEntity<>(response.getMessage());
        } else {
            return  CustomResponseEntity.error(response.getMessage());
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

    // Update chequebook by ID
    @PutMapping("/updateChequebook")
    public CustomResponseEntity<ChequebookDto> updateChequebookRequest(
            @RequestBody ChequebookDto chequebookDto,
            @RequestParam("accountNumber") String accountNumber) {
        return chequebookService.updateChequebookRequest(chequebookDto, accountNumber);
    }
    // Delete chequebook by ID
    @DeleteMapping("/deleteChequebook/{id}")
    public CustomResponseEntity<ChequebookDto> deleteChequebook(@PathVariable Long id) {
        return chequebookService.deleteChequebook(id);
    }
}
