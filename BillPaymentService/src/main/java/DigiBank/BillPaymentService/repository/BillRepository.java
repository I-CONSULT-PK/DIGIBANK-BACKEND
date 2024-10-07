package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.constants.BillStatus;
import DigiBank.BillPaymentService.constants.UtilityType;
import DigiBank.BillPaymentService.model.entity.Account;
import DigiBank.BillPaymentService.model.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BillRepository extends JpaRepository<Bill,Long> {

    @Query("SELECT b FROM Bill b WHERE b.account = :account AND b.account.biller.serviceCode = :serviceCode AND b.account.biller.utilityType = :utilityType")
    Bill findBillsByAccountAndBillerServiceCodeAndBillerUtilityType(
            @Param("account") Account account,
            @Param("serviceCode") String serviceCode,
            @Param("utilityType") UtilityType utilityType);

    @Query("SELECT b FROM Bill b WHERE b.account = :account " +
            "AND b.account.biller.serviceCode = :serviceCode " +
            "AND b.account.biller.utilityType = :utilityType " +
            "AND b.status = :status")
    Bill findBillsByAccountAndBillerServiceCodeAndBillerUtilityTypeAndStatus(
            @Param("account") Account account,
            @Param("serviceCode") String serviceCode,
            @Param("utilityType") UtilityType utilityType,
            @Param("status") BillStatus status);

}
