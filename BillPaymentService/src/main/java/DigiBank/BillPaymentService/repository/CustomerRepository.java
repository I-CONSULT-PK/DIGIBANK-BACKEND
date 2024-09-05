package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

}
