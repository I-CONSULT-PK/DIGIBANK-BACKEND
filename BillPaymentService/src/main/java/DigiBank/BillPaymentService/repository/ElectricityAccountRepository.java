package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.model.entity.ElectricityAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectricityAccountRepository extends JpaRepository<ElectricityAccount, Long> {
}
