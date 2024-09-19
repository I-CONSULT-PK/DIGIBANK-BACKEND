package com.iconsult.topup.controller;

import com.iconsult.topup.model.dto.NetworkDTO;
import com.iconsult.topup.service.NetworkService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/network")
public class NetworkController {

    @Autowired
    private NetworkService networkService;

    @GetMapping("/all")
    public CustomResponseEntity<List<NetworkDTO>> getAllNetworks() {
        return networkService.getAllNetworks();
    }

    @GetMapping("/{id}")
    public NetworkDTO getNetworkById(@PathVariable Long id) {
        return networkService.getNetworkById(id);
    }

    @PostMapping
    public NetworkDTO createNetwork(@RequestBody NetworkDTO dto) {
        return networkService.saveNetwork(dto);
    }

    @PutMapping("/{id}")
    public NetworkDTO updateNetwork(@PathVariable Long id, @RequestBody NetworkDTO dto) {
        dto.setId(id);
        return networkService.saveNetwork(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteNetwork(@PathVariable Long id) {
        networkService.deleteNetwork(id);
    }

    /*@PostMapping
    public ResponseEntity createNetwork(@RequestBody Network createDTO) {
        Network createdNetwork = networkService.saveNetwork(createDTO);
        return ResponseEntity.ok(createdNetwork);
    }*/
}
