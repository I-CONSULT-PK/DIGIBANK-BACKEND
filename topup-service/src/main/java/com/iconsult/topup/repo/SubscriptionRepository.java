package com.iconsult.topup.repo;

import com.iconsult.topup.model.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {

    @Query("SELECT s FROM Subscription s WHERE s.topUpCustomer.id = :customerId AND s.mobilePackage.id = :packageId AND s.startDate > :startDate")
    Optional<Subscription> findByCustomerIdAndMobilePackageIdAndStartDateAfter(
            @Param("customerId") Long customerId,
            @Param("packageId") Long packageId,
            @Param("startDate") LocalDate startDate
    );
}
