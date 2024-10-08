package DigiBank.BillPaymentService.repository;

import DigiBank.BillPaymentService.constants.BillStatus;
import DigiBank.BillPaymentService.constants.UtilityType;
import DigiBank.BillPaymentService.model.entity.Consumer;
import DigiBank.BillPaymentService.model.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill,Long> {

//    @Query("SELECT b FROM Bill b WHERE b.consumer = :consumer AND b.consumer.biller.serviceCode = :serviceCode AND b.consumer.biller.utilityType = :utilityType")
//    Bill findBillsByConsumerAndBillerServiceCodeAndBillerUtilityType(
//            @Param("consumer") Consumer consumer,
//            @Param("serviceCode") String serviceCode,
//            @Param("utilityType") UtilityType utilityType);
@Query("SELECT b FROM Bill b " +
        "JOIN b.consumer c " +
        "JOIN c.biller bil " +
        "WHERE c.id = :consumerId " +
        "AND bil.serviceCode = :serviceCode " +
        "AND bil.utilityType = :utilityType " +
        "AND b.status = :status")
Bill findBillByConsumerServiceAndUtilityAndStatus(
        @Param("consumerId") Long consumerId,
        @Param("serviceCode") String serviceCode,
        @Param("utilityType") UtilityType utilityType,
        @Param("status") BillStatus status);
//
//    @Query("SELECT b FROM Bill b WHERE b.account = :account " +
//            "AND b.account.biller.serviceCode = :serviceCode " +
//            "AND b.account.biller.utilityType = :utilityType " +
//            "AND b.status = :status")
//    Bill findBillsByConsumerAndBillerServiceCodeAndBillerUtilityTypeAndStatus(
//            @Param("account") Consumer consumer,
//            @Param("serviceCode") String serviceCode,
//            @Param("utilityType") UtilityType utilityType,
//            @Param("status") BillStatus status);


    Optional<Bill> findByConsumer_IdAndStatus(Long consumerId, BillStatus status);

}
