package DigiBank.BillPaymentService.service;

import DigiBank.BillPaymentService.model.dto.request.PaymentRequest;
import DigiBank.BillPaymentService.model.dto.response.PaymentResponse;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

    public PaymentResponse processPayment(PaymentRequest request);

    public PaymentResponse createPayment(String consumerNumber);

    ResponseEntity makePayment(String consumerNumber);
}
