package com.iconsult.topup.service.Impl;

import com.iconsult.topup.model.dto.MobilePackageDTO;
import com.iconsult.topup.model.dto.NetworkDTO;
import com.iconsult.topup.model.entity.MobilePackage;
import com.iconsult.topup.model.entity.Network;
import com.iconsult.topup.repo.MobilePackageRepository;
import com.iconsult.topup.repo.NetworkRepository;
import com.iconsult.topup.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NetworkServiceImpl implements NetworkService {

    @Autowired
    private NetworkRepository networkRepository;

    @Autowired
    private MobilePackageRepository mobilePackageRepository;

    @Override
    public List<NetworkDTO> getAllNetworks() {
        return networkRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NetworkDTO getNetworkById(Long id) {
        return networkRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Network not found"));
    }

    @Override
    public NetworkDTO saveNetwork(NetworkDTO dto) {
        Network network = new Network();
        network.setName(dto.getName());

        // Save network and handle bidirectional relationships
        network = networkRepository.save(network);

        return convertToDTO(network);
    }

    @Override
    public void deleteNetwork(Long id) {
        networkRepository.deleteById(id);
    }

    private NetworkDTO convertToDTO(Network network) {
        NetworkDTO dto = new NetworkDTO();
        dto.setId(network.getId());
        dto.setName(network.getName());

        // Convert MobilePackage entities to DTOs
        List<MobilePackageDTO> mobilePackageDTOs = Optional.ofNullable(network.getMobilePackages())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        dto.setMobilePackages(mobilePackageDTOs);

        return dto;
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

/*    @Override
    public Optional<Network> getNetworkById(Long id) {
        return networkRepository.findById(id);
    }

    @Override
    public Network saveNetwork(Network network) {
        return networkRepository.save(network);
    }*/
}
