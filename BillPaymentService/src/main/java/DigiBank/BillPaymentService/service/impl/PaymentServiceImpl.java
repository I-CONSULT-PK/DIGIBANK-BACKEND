package DigiBank.BillPaymentService.service.impl;

import DigiBank.BillPaymentService.model.dto.request.PaymentRequest;
import DigiBank.BillPaymentService.model.dto.response.PaymentResponse;
import DigiBank.BillPaymentService.repository.ConsumerRepository;
import DigiBank.BillPaymentService.repository.BillerRepository;
import DigiBank.BillPaymentService.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private BillerRepository billerRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        return null;
    }

    @Override
    public PaymentResponse createPayment(String consumerNumber) {
        return null;
    }

    @Override
    public ResponseEntity makePayment(String consumerNumber) {

        return null;
    }
}
