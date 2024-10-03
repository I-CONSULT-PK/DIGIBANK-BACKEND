package DigiBank.BillPaymentService.service;

import DigiBank.BillPaymentService.constants.CustomResponseEntity;
import DigiBank.BillPaymentService.constants.UtilityType;
import DigiBank.BillPaymentService.model.dto.request.BillDto;
import DigiBank.BillPaymentService.model.dto.request.BillerDtoRequest;
import DigiBank.BillPaymentService.model.entity.Bill;
import org.springframework.http.ResponseEntity;

public interface BillService {

    public CustomResponseEntity addBiller (BillerDtoRequest request);

    CustomResponseEntity getBillDetailsByConsumerNumber(String consumerNumber, String serviceCode, String utilityType);

    CustomResponseEntity createBill(BillDto billDto);

    CustomResponseEntity getAllBillers(String utilityType);

    CustomResponseEntity getAllUtilityTypes();
}
