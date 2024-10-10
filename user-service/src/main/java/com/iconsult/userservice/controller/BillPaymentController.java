package com.iconsult.userservice.controller;


import com.iconsult.userservice.model.dto.request.BillPaymentDto;
import com.iconsult.userservice.model.dto.request.ScheduleBillPaymentRequest;
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

    @GetMapping("/getBillers")
    public CustomResponseEntity getAllBillers(@RequestParam("utilityType") String utilityType ){
        return this.billPaymentService.getAllBillProviders(utilityType);
    }

    @GetMapping("/getUtilityTypes")
    public CustomResponseEntity getUtilityTypes (){
        return this.billPaymentService.getUtilityTypes();
    }

    @PostMapping("/payBill")
    public ResponseEntity<CustomResponseEntity> getUtilityDetails(
            @RequestParam(name = "billId")  Long billId,
            @RequestParam(name = "accountNumber") String accountNumber) {

        try {
            // Call service method to process the request
            CustomResponseEntity response = billPaymentService.payBill(billId,accountNumber);

            // Return the response with HTTP 200 OK
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            // Return an error response with HTTP 500 Internal Server Error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/scheduleBillPay")
    public ResponseEntity<CustomResponseEntity> schdeuleBillPayment(
            @RequestBody ScheduleBillPaymentRequest scheduleBillPaymentRequest
            ) {

        try {
            // Call service method to process the request
            CustomResponseEntity response = billPaymentService.schedulePayBill(scheduleBillPaymentRequest);

            // Return the response with HTTP 200 OK
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            // Return an error response with HTTP 500 Internal Server Error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
