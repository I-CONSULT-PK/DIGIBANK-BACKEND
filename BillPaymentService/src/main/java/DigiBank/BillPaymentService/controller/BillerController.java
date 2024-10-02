package DigiBank.BillPaymentService.controller;

import DigiBank.BillPaymentService.constants.CustomResponseEntity;
import DigiBank.BillPaymentService.constants.UtilityType;
import DigiBank.BillPaymentService.model.dto.request.BillerDtoRequest;
import DigiBank.BillPaymentService.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/billpayment")
public class BillerController {

    @Autowired
    private BillService service;

    @PostMapping("/addBiller")
    public CustomResponseEntity addBiller(@RequestBody BillerDtoRequest request) {
        return service.addBiller(request);
    }

    @GetMapping("/getUtilityTypes")
    public CustomResponseEntity getAllUtilityTypes() {
        return this.service.getAllUtilityTypes();
    }


    @GetMapping("/getBillers")
    public CustomResponseEntity getBillers(@RequestParam("utilityType") UtilityType utilityType) {
        return this.service.getAllBillers(utilityType);
    }
}
