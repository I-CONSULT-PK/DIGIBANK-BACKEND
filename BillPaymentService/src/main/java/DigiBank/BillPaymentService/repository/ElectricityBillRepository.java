package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.model.entity.ElectricityBill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectricityBillRepository extends JpaRepository<ElectricityBill, Long> {
}
