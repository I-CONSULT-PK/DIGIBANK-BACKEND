package com.iconsult.topup.repo;

import com.iconsult.topup.model.entity.MobilePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MobilePackageRepository extends JpaRepository<MobilePackage, Long> {
//    List<MobilePackage> findAllByNetworkId(Long networkId);

    List<MobilePackage> findByNetworkId(Long networkId);
}
