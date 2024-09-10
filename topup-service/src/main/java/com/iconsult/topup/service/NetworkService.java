package com.iconsult.topup.service;

import com.iconsult.topup.model.dto.NetworkDTO;

import java.util.List;

public interface NetworkService {

     /*Optional<Network> getNetworkById(Long id);

     Network saveNetwork(Network network);*/

      List<NetworkDTO> getAllNetworks();

      NetworkDTO getNetworkById(Long id);

      NetworkDTO saveNetwork(NetworkDTO dto);

      void deleteNetwork(Long id);

}
