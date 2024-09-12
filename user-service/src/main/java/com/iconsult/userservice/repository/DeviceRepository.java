package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.entity.Device;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findByCustomer(Customer c);

    @Query("SELECT d.pinHash FROM Device d JOIN d.customer c WHERE c.accountNumber = :accountNumber AND d.pinHash = :pinHash")
    String findPinHashByAccountNumberAndPinHash(@Param("accountNumber") String accountNumber, @Param("pinHash") String pinHash);

//    Device findByDeviceId(String deviceId);
//    List<Device> findByCustomerIdAndVerified(Long customerId, Boolean verified);

    @Query("SELECT d FROM Device d WHERE d.customer.id = :customerId AND d.devicePin = :devicePin AND d.unique1 = :uniquePin")
    Device findDevicesByCustomerIdAndDevicePinAndUniquePin(
            @Param("customerId") Long customerId,
            @Param("devicePin") String devicePin,
            @Param("uniquePin") String uniquePin
    );

}
