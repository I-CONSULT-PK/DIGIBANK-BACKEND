package com.iconsult.topup.service.Impl;

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

        TopUpCustomer customer = customerOptional.get();

        Optional<MobilePackage> mobilePackageOptional = mobilePackageRepository.findById(packageId);
        if (!mobilePackageOptional.isPresent()) {
            return CustomResponseEntity.error("Mobile package not found for the given package ID.");
        }

        MobilePackage mobilePackage = mobilePackageOptional.get();

        Network network = mobilePackage.getNetwork();
        if (network == null) {
            return CustomResponseEntity.error("Network not found for the mobile package.");
        }


        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        Optional<Subscription> existingSubscription = subscriptionRepository.findByCustomerIdAndMobilePackageIdAndStartDateAfter(
                customer.getId(), packageId, thirtyDaysAgo);

        if (existingSubscription.isPresent()) {
            return CustomResponseEntity.error("You are already subscribed to this package within the last 30 days. " +
                    "Please wait until the current subscription expires or contact support for assistance");
        }

        // Create and save new subscription
        Subscription subscription = new Subscription();
        subscription.setTopUpCustomer(customer);
        subscription.setMobilePackage(mobilePackage);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusDays(mobilePackage.getValidityDays()));

        subscriptionRepository.save(subscription);

        // Create and save top-up transaction
        TopUpTransaction topUpTransaction = new TopUpTransaction();
        topUpTransaction.setTopUpCustomer(customer);
        topUpTransaction.setTransactionDate(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
        topUpTransaction.setTopUpType(TopUpType.MOBILE_PACKAGE);
        topUpTransaction.setAmount(mobilePackage.getPrice());

        topUpTransactionRepository.save(topUpTransaction);

        MobilePackageDTO dto = new MobilePackageDTO();
        dto.setValidityDays(mobilePackage.getValidityDays());
        dto.setId(packageId);
        dto.setDescription(mobilePackage.getDescription());
        dto.setPrice(mobilePackage.getPrice());
        dto.setNetworkId(mobilePackage.getNetwork().getId());
        dto.setPkg_name(mobilePackage.getName());

        return new CustomResponseEntity(dto,"Subscription successful. Package belongs to network: " + network.getName());
    }
}
