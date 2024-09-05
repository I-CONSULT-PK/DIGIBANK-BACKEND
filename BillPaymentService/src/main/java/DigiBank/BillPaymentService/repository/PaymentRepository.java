package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
