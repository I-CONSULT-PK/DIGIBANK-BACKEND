package com.iconsult.topup.service;

import com.iconsult.topup.model.dto.NetworkDTO;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.util.List;

public interface NetworkService {

     /*Optional<Network> getNetworkById(Long id);

     Network saveNetwork(Network network);*/

      CustomResponseEntity<List<NetworkDTO>> getAllNetworks();

      NetworkDTO getNetworkById(Long id);

      NetworkDTO saveNetwork(NetworkDTO dto);

      void deleteNetwork(Long id);

}
