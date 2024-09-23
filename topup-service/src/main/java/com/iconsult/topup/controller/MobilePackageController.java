package com.iconsult.topup.controller;

import com.iconsult.topup.model.dto.MobilePackageDTO;
import com.iconsult.topup.service.MobilePackageService;
import com.iconsult.topup.service.NetworkService;
import com.iconsult.topup.service.SubscribeService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/packages")
public class MobilePackageController {

    @Autowired
    private MobilePackageService mobilePackageService;

    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private NetworkService networkService;

    @GetMapping("/all")
    public List<MobilePackageDTO> getAllMobilePackages() {
        return mobilePackageService.getAllMobilePackages();
    }

    @GetMapping("/{id}")
    public MobilePackageDTO getMobilePackageById(@PathVariable Long id) {
        return mobilePackageService.getMobilePackageById(id);
    }

    @PostMapping("/createPackage")
    public CustomResponseEntity createPackage(@RequestBody MobilePackageDTO packageDTO){
        return mobilePackageService.createPackage(packageDTO);
    }
    @GetMapping("/network/{networkId}")
    public List<MobilePackageDTO> getMobilePackagesByNetworkId(@PathVariable Long networkId) {
        return mobilePackageService.getMobilePackagesByNetworkId(networkId);
    }

    @GetMapping("/subscribePackage")
    public CustomResponseEntity subscribePackage(@RequestParam("packageId") Long packageId,
                                           @RequestParam("mobileNumber") String mobileNumber){
        return subscribeService.subscribeToPackage(mobileNumber,packageId);
    }

    @DeleteMapping("/{id}")
    public void deleteMobilePackage(@PathVariable Long id) {
        mobilePackageService.deleteMobilePackage(id);
    }

}
