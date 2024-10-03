package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.constants.UtilityType;
import DigiBank.BillPaymentService.model.entity.Biller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillerRepository extends JpaRepository<Biller,Long> {
    List<Biller> findByUtilityType(UtilityType utilityType);
    Optional<Biller> findByName(String name);

    Optional<Biller> findByContactNumber(String contactNumber);
}
