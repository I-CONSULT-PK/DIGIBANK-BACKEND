package com.iconsult.topup.repo;

import com.iconsult.topup.model.entity.MobilePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MobilePackageRepository extends JpaRepository<MobilePackage, Long> {
//    List<MobilePackage> findAllByNetworkId(Long networkId);

    List<MobilePackage> findByNetworkId(Long networkId);

    Optional<MobilePackage> findByPkgNameAndNetwork_Id(String pkgName, Long networkId);

    Optional<MobilePackage> findByPkgNameAndNetwork_IdAndBundleCategory(
            String pkgName, Long networkId, String bundleCategory);
}
