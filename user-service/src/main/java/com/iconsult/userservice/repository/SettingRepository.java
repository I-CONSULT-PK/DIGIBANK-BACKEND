package com.iconsult.userservice.repository;

import com.iconsult.userservice.constant.PinStatus;
import com.iconsult.userservice.model.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Device, Long> {

    Device findByCustomerIdAndUnique1AndPinStatus(Long customerId, String unique, PinStatus pinStatus);

    Optional<Device> findByUnique1(String unique);

}
