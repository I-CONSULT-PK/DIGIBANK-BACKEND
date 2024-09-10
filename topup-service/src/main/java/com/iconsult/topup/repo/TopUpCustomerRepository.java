package com.iconsult.topup.repo;

import com.iconsult.topup.constants.CarrierType;
import com.iconsult.topup.model.entity.TopUpCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TopUpCustomerRepository  extends JpaRepository<TopUpCustomer, Long> {

    Optional<TopUpCustomer> findByMobileNumber(String mobileNumber);

    @Query("SELECT t FROM TopUpCustomer t WHERE t.mobileNumber = :mobileNumber AND t.carrierType = :carrierType")
    Optional<TopUpCustomer> findByMobileNumberAndCarrierType(@Param("mobileNumber") String mobileNumber, @Param("carrierType") CarrierType carrierType);

}
