package com.iconsult.userservice.controller;


import com.iconsult.userservice.model.dto.request.BillPaymentDto;
import com.iconsult.userservice.service.BillPaymentService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/v1/billPayment")
public class BillPaymentController {

    @Autowired
    private BillPaymentService billPaymentService;

    @PostMapping("/details")
    public ResponseEntity<CustomResponseEntity> getUtilityDetails(
            @RequestParam(name = "consumerNumber")  String consumerNumber,
            @RequestParam(name = "serviceCode")  String serviceCode,
            @RequestParam(name = "utilityType")  String utilityType,
            @RequestBody BillPaymentDto billPaymentDto) {

        try {
            // Call service method to process the request
            CustomResponseEntity response = billPaymentService.getUtilityDetails(consumerNumber, serviceCode, utilityType,billPaymentDto);

            // Return the response with HTTP 200 OK
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            // Return an error response with HTTP 500 Internal Server Error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
