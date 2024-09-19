package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.dto.response.ChequeDto;
import com.iconsult.userservice.service.ChequeService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/cheques")
public class ChequeController {

    @Autowired
    private ChequeService chequeService;

    // Create a new cheque
//    @PostMapping("/createCheque")
//    public CustomResponseEntity<ChequeDto> createCheque(@RequestBody ChequeDto chequeDto) {
//        return chequeService.createCheque(chequeDto);
//    }


    // Get cheque by ID
    @GetMapping("/getCheque")
    public CustomResponseEntity<ChequeDto> getChequeById(@RequestParam("chequeNumber") String chequeNumber) {
        return chequeService.getCheque(chequeNumber);
    }

    // Cancel Cheque Status API
    @PutMapping("/cancel")
    public CustomResponseEntity<?> cancelCheque(@RequestParam("chequeNumber") String chequeNumber) {
        CustomResponseEntity<ChequeDto> response = chequeService.cancelCheque(new ChequeDto(), chequeNumber);

        if (response.isSuccess()) {
            return new CustomResponseEntity<>(response.getData(), "Success");
        } else {
            return CustomResponseEntity.error(response.getMessage());
        }
    }

    // Get all cheques
    @GetMapping("/getAllCheques")
    public CustomResponseEntity<List<ChequeDto>> getAllCheques() {
        return chequeService.getAllCheques();
    }

    // Update cheque by ID
    @PutMapping("/updateCheque")
    public CustomResponseEntity<ChequeDto> updateCheque(@RequestParam("chequeNumber") String chequeNumber, @RequestBody ChequeDto chequeDto) {
        return chequeService.updateCheque(chequeNumber, chequeDto);
    }

    // Delete cheque by ID
    @DeleteMapping("/deleteCheque")
    public CustomResponseEntity<ChequeDto> deleteCheque(@RequestParam("chequeNumber") String chequeNumber) {
        return chequeService.deleteCheque(chequeNumber);
    }

}