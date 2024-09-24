package com.iconsult.topup.service.Impl;

import com.iconsult.topup.constants.CarrierType;
import com.iconsult.topup.constants.TopUpType;
import com.iconsult.topup.model.dto.MobilePackageDTO;
import com.iconsult.topup.model.entity.*;
import com.iconsult.topup.repo.MobilePackageRepository;
import com.iconsult.topup.repo.SubscriptionRepository;
import com.iconsult.topup.repo.TopUpCustomerRepository;
import com.iconsult.topup.repo.TopUpTransactionRepository;
import com.iconsult.topup.service.SubscribeService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SubscribeServiceImpl implements SubscribeService {

    @Autowired
    private TopUpCustomerRepository topUpCustomerRepository;

    @Autowired
    private MobilePackageRepository mobilePackageRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TopUpTransactionRepository topUpTransactionRepository;
    @Override
    public CustomResponseEntity subscribeToPackage(String mobileNumber, Long packageId) {




        Optional<TopUpCustomer> customerOptional = topUpCustomerRepository.findByMobileNumber(mobileNumber);
        if (!customerOptional.isPresent()) {
            return CustomResponseEntity.error("Customer not found for the given mobile number.");
        }

        Long customerId = customerOptional.get().getId();

        Optional<MobilePackage> mobilePackageOptional = mobilePackageRepository.findById(packageId);
        if (!mobilePackageOptional.isPresent()) {
            return CustomResponseEntity.error("Mobile package not found for the given package ID.");
        }


        MobilePackage mobilePackage = mobilePackageOptional.get();

        String networkName = mobilePackage.getNetwork().getName();
        CarrierType carrierType = customerOptional.get().getCarrierType();
        if(!carrierType.name().equalsIgnoreCase(networkName)){
            String errorMessage = String.format(
                    "Carrier type mismatch: Customer's carrier is '%s', but the selected package belongs to network '%s'. Please choose a compatible package.",
                    carrierType.name(), networkName
            );
            return CustomResponseEntity.error(errorMessage);
        }

        Network network = mobilePackage.getNetwork();
        if (network == null) {
            return CustomResponseEntity.error("Network not found for the mobile package.");
        }
        
        LocalDate today = LocalDate.now();
        List<Subscription> activeSubscription = subscriptionRepository.findActiveSubscription(customerId,packageId,today);

        if (!activeSubscription.isEmpty()) {
            return CustomResponseEntity.error("You are already subscribed to this package");
        }

        // Create and save new subscription
        Subscription subscription = new Subscription();
        subscription.setTopUpCustomer(customerOptional.get());
        subscription.setMobilePackage(mobilePackage);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusDays(mobilePackage.getValidityDays()));

        subscriptionRepository.save(subscription);

        // Create and save top-up transaction
        TopUpTransaction topUpTransaction = new TopUpTransaction();
        topUpTransaction.setTopUpCustomer(customerOptional.get());
        topUpTransaction.setTransactionDate(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
        topUpTransaction.setTopUpType(TopUpType.MOBILE_PACKAGE);
        topUpTransaction.setAmount(mobilePackage.getPrice());

        topUpTransactionRepository.save(topUpTransaction);

        MobilePackageDTO dto = new MobilePackageDTO();
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

        Map<String,Object> map = new HashMap<>();
        map.put("Network Name ", network.getName());
        map.put("package details",dto);

        return new CustomResponseEntity(map,"Subscription successful");
    }
}
