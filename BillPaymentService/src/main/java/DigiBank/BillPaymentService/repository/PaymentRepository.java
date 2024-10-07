package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.model.entity.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<BillPayment,Long> {
}
