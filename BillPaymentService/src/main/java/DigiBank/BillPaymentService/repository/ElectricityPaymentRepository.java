package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.model.entity.ElectricityPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectricityPaymentRepository  extends JpaRepository<ElectricityPayment, Long> {
}
