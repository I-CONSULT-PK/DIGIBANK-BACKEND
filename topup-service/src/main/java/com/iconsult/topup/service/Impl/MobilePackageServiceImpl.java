package com.iconsult.topup.service.Impl;

import com.iconsult.topup.model.dto.MobilePackageDTO;
import com.iconsult.topup.model.entity.MobilePackage;
import com.iconsult.topup.model.entity.Network;
import com.iconsult.topup.exception.ExceptionNotFound;
import com.iconsult.topup.repo.MobilePackageRepository;
import com.iconsult.topup.repo.NetworkRepository;
import com.iconsult.topup.service.MobilePackageService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MobilePackageServiceImpl implements MobilePackageService {

    @Autowired
    private MobilePackageRepository mobilePackageRepository;

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
        mobilePackage.setPkg_name(dto.getPkg_name());
        mobilePackage.setDescription(dto.getDescription());
        mobilePackage.setPrice(dto.getPrice());
        mobilePackage.setData_limit(dto.getData_limit());
        mobilePackage.setNetwork(network);

        mobilePackage = mobilePackageRepository.save(mobilePackage);

        return convertToDTO(mobilePackage);
    }

    public void deleteMobilePackage(Long id) {
        mobilePackageRepository.deleteById(id);
    }

    @Override
    public CustomResponseEntity getPackageDetails(Long networkId, Long packageId) {

        Optional<Network> network = networkRepository.findById(networkId);

        if(!network.isPresent()){
            return CustomResponseEntity.error("invalid network id");
        }
        List<MobilePackage> mobilePackages = network.get().getMobilePackages();

        MobilePackageDTO mobilePackageDTO = mobilePackages.stream().filter(mobilePackage -> mobilePackage.getId().equals(packageId))
                .findFirst()
                .map(this::convertToDTO)
                .orElse(null);
        if(mobilePackageDTO==null){
            return CustomResponseEntity.error("invalid package id");
        }

        return new CustomResponseEntity(mobilePackageDTO,"Package Details");
    }

    private MobilePackageDTO convertToDTO(MobilePackage mobilePackage) {
        MobilePackageDTO dto = new MobilePackageDTO();
        dto.setId(mobilePackage.getId());
        dto.setPkg_name(mobilePackage.getPkg_name());
        dto.setDescription(mobilePackage.getDescription());
        dto.setPrice(mobilePackage.getPrice());
        dto.setData_limit(mobilePackage.getData_limit());
        dto.setNetworkId(mobilePackage.getNetwork() != null ? mobilePackage.getNetwork().getId() : null);

        return dto;
    }


   /* @Override
    public List<MobilePackage> getAllPackages() {
        return mobilePackageRepository.findAll();
    }

    @Override
    public Optional<MobilePackage> getPackageById(Long id) {
        return mobilePackageRepository.findById(id);
    }

    @Override
    public MobilePackage savePackage(MobilePackage mobilePackage) {
        return mobilePackageRepository.save(mobilePackage);
    }

    @Override
    public void deletePackage(Long id) {
        mobilePackageRepository.deleteById(id);
    }

    @Override
    public List<MobilePackage> getPackagesByNetworkId(Long networkId) {
        return mobilePackageRepository.findAllByNetworkId(networkId);
    }*/


}
