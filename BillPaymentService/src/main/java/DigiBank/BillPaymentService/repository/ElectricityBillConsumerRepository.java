package DigiBank.BillPaymentService.repository;
import DigiBank.BillPaymentService.model.entity.ElectricityBillConsumer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectricityBillConsumerRepository extends JpaRepository<ElectricityBillConsumer, Long> {
}
