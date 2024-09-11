package com.iconsult.topup.service;

import com.iconsult.topup.model.dto.MobilePackageDTO;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.util.List;

public interface MobilePackageService {

     /*List<MobilePackage> getAllPackages();

     Optional<MobilePackage> getPackageById(Long id);

     MobilePackage savePackage(MobilePackage mobilePackage);

     void deletePackage(Long id);

     List<MobilePackage> getPackagesByNetworkId(Long networkId);*/

      List<MobilePackageDTO> getAllMobilePackages();

      MobilePackageDTO getMobilePackageById(Long id);

      List<MobilePackageDTO> getMobilePackagesByNetworkId(Long networkId);

      MobilePackageDTO saveMobilePackage(MobilePackageDTO dto);

      void deleteMobilePackage(Long id);

      CustomResponseEntity getPackageDetails(String mobileNumber,Long networkId, Long packageId);
}
