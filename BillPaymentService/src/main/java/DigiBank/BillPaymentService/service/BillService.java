package DigiBank.BillPaymentService.service;

import DigiBank.BillPaymentService.constants.CustomResponseEntity;
import DigiBank.BillPaymentService.model.dto.request.BillDto;
import DigiBank.BillPaymentService.model.dto.request.BillerDtoRequest;
import DigiBank.BillPaymentService.model.entity.Bill;
import org.springframework.http.ResponseEntity;

public interface BillService {

    public ResponseEntity addBiller (BillerDtoRequest request);

    CustomResponseEntity getBillDetailsByConsumerNumber(String consumerNumber, String serviceCode, String utilityType);

    CustomResponseEntity createBill(BillDto billDto);
}
