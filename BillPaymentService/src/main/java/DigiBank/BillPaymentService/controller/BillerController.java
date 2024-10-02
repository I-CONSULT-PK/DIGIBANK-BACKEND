package DigiBank.BillPaymentService.controller;

import DigiBank.BillPaymentService.constants.CustomResponseEntity;
import DigiBank.BillPaymentService.model.dto.request.BillerDtoRequest;
import DigiBank.BillPaymentService.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/billpayment")
public class BillerController {

    @Autowired
    private BillService service;

    @PostMapping("/addBiller")
    public CustomResponseEntity addBiller (@RequestBody BillerDtoRequest request){
        return service.addBiller(request);
    }


    @GetMapping("/getBillers")
    public CustomResponseEntity getBillers (){
        return this.service.getAllBillers();
    }
}
