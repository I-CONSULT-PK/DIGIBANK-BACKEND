package com.iconsult.topup.repo;

import com.iconsult.topup.constants.CarrierType;
import com.iconsult.topup.model.entity.TopUpCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopUpCustomerRepository  extends JpaRepository<TopUpCustomer, Long> {

    Optional<TopUpCustomer> findByMobileNumber(String mobileNumber);
    Optional<TopUpCustomer> findByMobileNumberAndCarrierType(String mobileNumber, CarrierType carrierType);

}
