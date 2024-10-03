package DigiBank.BillPaymentService.controller;

import DigiBank.BillPaymentService.constants.CustomResponseEntity;
import DigiBank.BillPaymentService.constants.UtilityType;
import DigiBank.BillPaymentService.model.dto.request.BillDto;
import DigiBank.BillPaymentService.model.dto.request.BillerDtoRequest;
import DigiBank.BillPaymentService.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/billpayment")
public class BillPaymentController {

    @Autowired
    private BillService billService;

    @GetMapping("/getBillDetails")
    public CustomResponseEntity getBillDetailsByConsumerNumber(@RequestParam("consumerNumber") String consumerNumber,
                                                               @RequestParam("serviceCode") String serviceCode,
                                                               @RequestParam("utilityType") String utilityType) {
        return billService.getBillDetailsByConsumerNumber(consumerNumber, serviceCode, utilityType);

    }
    @PostMapping("/createBill")
    public CustomResponseEntity createBill(@RequestBody BillDto billDto) {
        return  billService.createBill(billDto);
    }

    @PostMapping("/addBiller")
    public CustomResponseEntity addBiller(@RequestBody BillerDtoRequest request) {
        return billService.addBiller(request);
    }

    @GetMapping("/getUtilityTypes")
    public CustomResponseEntity getAllUtilityTypes() {
        return this.billService.getAllUtilityTypes();
    }


    @GetMapping("/getBillers")
    public CustomResponseEntity getBillers(@RequestParam("utilityType") UtilityType utilityType) {
        return this.billService.getAllBillers(utilityType);
    }

}
