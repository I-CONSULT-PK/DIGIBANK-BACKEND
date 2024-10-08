package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.model.entity.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerRepository extends JpaRepository<Consumer,Long> {

    Consumer findByConsumerNumber(String consumerNumber);
}
