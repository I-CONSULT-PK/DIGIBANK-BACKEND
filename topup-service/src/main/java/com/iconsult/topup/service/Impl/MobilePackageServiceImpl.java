package com.iconsult.topup.service.Impl;

import com.iconsult.topup.exception.ExceptionNotFound;
import com.iconsult.topup.model.dto.MobilePackageDTO;
import com.iconsult.topup.model.entity.MobilePackage;
import com.iconsult.topup.model.entity.Network;
import com.iconsult.topup.repo.MobilePackageRepository;
import com.iconsult.topup.repo.NetworkRepository;
import com.iconsult.topup.repo.TopUpCustomerRepository;
import com.iconsult.topup.repo.TopUpTransactionRepository;
import com.iconsult.topup.service.MobilePackageService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MobilePackageServiceImpl implements MobilePackageService {

    @Autowired
    private MobilePackageRepository mobilePackageRepository;

    @Autowired
    private TopUpTransactionRepository topUpTransactionRepository;

    @Autowired
    private TopUpCustomerRepository topUpCustomerRepository;
    @Autowired
    private NetworkRepository networkRepository;

    public List<MobilePackageDTO> getAllMobilePackages() {
        return mobilePackageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MobilePackageDTO getMobilePackageById(Long id) {
        return mobilePackageRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ExceptionNotFound("MobilePackage not found with id: "+id));
    }

    public List<MobilePackageDTO> getMobilePackagesByNetworkId(Long networkId) {
        return mobilePackageRepository.findByNetworkId(networkId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MobilePackageDTO saveMobilePackage(MobilePackageDTO dto) {
        Network network = networkRepository.findById(dto.getNetworkId())
                .orElseThrow(() -> new RuntimeException("Network not found"));

        MobilePackage mobilePackage = new MobilePackage();
       // mobilePackage.setName(dto.getPkg_name());
       // mobilePackage.setDescription(dto.getDescription());
        mobilePackage.setPrice(dto.getPrice());
        mobilePackage.setNetwork(network);

        mobilePackage = mobilePackageRepository.save(mobilePackage);

        return convertToDTO(mobilePackage);
    }

    public void deleteMobilePackage(Long id) {
        mobilePackageRepository.deleteById(id);
    }

    @Override
    public CustomResponseEntity createPackage(MobilePackageDTO packageDTO) {

        if (packageDTO.getNetworkId() != null) {
            if (!networkRepository.existsById(packageDTO.getNetworkId())) {
                return CustomResponseEntity.error("Network not found with ID: " + packageDTO.getNetworkId());
            }
        }

//        Optional<MobilePackage> existingPkg = mobilePackageRepository.findByPkgNameAndNetwork_Id(packageDTO.getPkgName(), packageDTO.getNetworkId());
        Optional<MobilePackage> existingPkg = mobilePackageRepository.findByPkgNameAndNetwork_IdAndBundleCategory(
                packageDTO.getPkgName(),
                packageDTO.getNetworkId(),
                packageDTO.getBundleCategory()
        );
        if (existingPkg.isPresent()) {
            return CustomResponseEntity.error("Package with name '" + packageDTO.getPkgName() +
                    "' for network ID " + packageDTO.getNetworkId() +
                    " and category '" + packageDTO.getBundleCategory() + "' already exists.");
        }

        MobilePackage mobilePackage = convertToEntity(packageDTO, networkRepository);
        mobilePackageRepository.save(mobilePackage);

        Map<String , Object> map = new HashMap<>();
        map.put("package",packageDTO);
        map.put("networkName", mobilePackage.getNetwork().getName());
        return new CustomResponseEntity(map,"package created!");

    }

    private MobilePackageDTO convertToDTO(MobilePackage mobilePackage) {
        MobilePackageDTO dto = new MobilePackageDTO();

        dto.setPkgId(mobilePackage.getId());
        dto.setPkgName(mobilePackage.getPkgName());
        dto.setPrice(mobilePackage.getPrice());
        dto.setTotalGBs(mobilePackage.getGBs());
        dto.setSocialGBs(mobilePackage.getSocialGBs());
        dto.setBundleCategory(mobilePackage.getBundleCategory());
        dto.setValidityDays(mobilePackage.getValidityDays());
        dto.setOffNetMints(mobilePackage.getOffNetMints());
        dto.setOnNetMints(mobilePackage.getOnNetMints());
        dto.setSmsCount(mobilePackage.getSmsCount());
        dto.setNetworkId(mobilePackage.getNetwork() != null ? mobilePackage.getNetwork().getId() : null);
        return dto;
    }

    private MobilePackage convertToEntity(MobilePackageDTO packageDTO, NetworkRepository networkRepository) {
        MobilePackage pkg = new MobilePackage();

        pkg.setPkgName(packageDTO.getPkgName());
        pkg.setPrice(packageDTO.getPrice());

        pkg.setGBs(packageDTO.getTotalGBs());
        pkg.setSocialGBs(packageDTO.getSocialGBs());
        pkg.setBundleCategory(packageDTO.getBundleCategory());
        pkg.setValidityDays(packageDTO.getValidityDays());
        pkg.setOffNetMints(packageDTO.getOffNetMints());
        pkg.setOnNetMints(packageDTO.getOnNetMints());
        pkg.setSmsCount(packageDTO.getSmsCount());

        if (packageDTO.getNetworkId() != null) {
           Network network = networkRepository.findById(packageDTO.getNetworkId()).orElse(null);
            pkg.setNetwork(network);
        }

        return pkg;
    }


}
