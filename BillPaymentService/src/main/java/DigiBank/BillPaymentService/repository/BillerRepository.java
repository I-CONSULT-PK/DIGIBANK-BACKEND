package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.model.entity.Biller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillerRepository extends JpaRepository<Biller,Long> {
}
