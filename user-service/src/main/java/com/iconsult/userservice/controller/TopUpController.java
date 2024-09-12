package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.dto.request.BillPaymentDto;
import com.iconsult.userservice.service.TopUpService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/topup")
public class TopUpController {

    @Autowired
    TopUpService topUpService;
    @PostMapping("/topUp")
    public ResponseEntity<CustomResponseEntity> getUtilityDetails(
            @RequestParam(name = "phoneNumber")  String phoneNumber,
            @RequestParam(name = "amount")  Double amount,
            @RequestParam(name = "carrier")  String carrier,
            @RequestParam(name = "plan")  String plan,
            @RequestParam(name = "accountNumber")  String accountNumber) {

        try {
            // Call service method to process the request
            CustomResponseEntity response = topUpService.getMobileNumberAndPlanDetail(phoneNumber,amount, carrier, plan, accountNumber);

            // Return the response with HTTP 200 OK
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            // Return an error response with HTTP 500 Internal Server Error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/getPackages")
    public ResponseEntity<CustomResponseEntity> getAllNetworkPackages() {

        try {
            // Call service method to process the request
            CustomResponseEntity response = topUpService.getAllNetworkPackages();

            // Return the response with HTTP 200 OK
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            // Return an error response with HTTP 500 Internal Server Error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/packageTransaction")
    public ResponseEntity<CustomResponseEntity> packageTransaction(
            @RequestParam(name = "packageId")  Long packageId,
            @RequestParam(name = "accountNumber")  String accountNumber,
            @RequestParam(name = "mobileNumber")  String mobileNumber) {

        try {
            // Call service method to process the request
            CustomResponseEntity response = topUpService.packageTransaction(packageId,accountNumber,mobileNumber);

            // Return the response with HTTP 200 OK
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            // Return an error response with HTTP 500 Internal Server Error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }






}
