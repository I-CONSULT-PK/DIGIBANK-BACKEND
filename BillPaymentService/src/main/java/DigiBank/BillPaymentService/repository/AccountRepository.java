package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {

    Account findByAccountNumber(String accountNumber);
}
