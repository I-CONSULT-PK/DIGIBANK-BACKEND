package DigiBank.BillPaymentService.controller;

import DigiBank.BillPaymentService.model.dto.request.BillerDtoRequest;
import DigiBank.BillPaymentService.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/billpayment")
public class BillerController {

    @Autowired
    private BillService service;

    @PostMapping("/addBiller")
    public ResponseEntity addBiller (@RequestBody BillerDtoRequest request){
        return service.addBiller(request);
    }
}
