package com.iconsult.topup.service.Impl;

import com.iconsult.topup.constants.CarrierType;
import com.iconsult.topup.constants.TopUpType;
import com.iconsult.topup.model.entity.TopUpCustomer;
import com.iconsult.topup.model.entity.TopUpTransaction;
import com.iconsult.topup.repo.TopUpCustomerRepository;
import com.iconsult.topup.repo.TopUpTransactionRepository;
import com.iconsult.topup.service.TopUpService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
public class TopUpServiceImpl implements TopUpService {

    @Autowired
    private TopUpTransactionRepository topUpTransactionRepository;

    @Autowired
    private  TopUpCustomerRepository topUpCustomerRepository;

    TopUpServiceImpl(){}
    public TopUpServiceImpl (TopUpTransactionRepository topUpTransactionRepository) {
        this.topUpTransactionRepository = topUpTransactionRepository;
    }


    @Override
    public CustomResponseEntity topUpTransaction(String mobileNumber, String carrier , Double amount, String plan) {

        CarrierType carrierType ;
        TopUpType topUpType;

        try {
            carrierType = CarrierType.valueOf(carrier.toUpperCase());
            topUpType = TopUpType.valueOf(plan.toUpperCase());
        }catch (IllegalArgumentException ex) {
            return new CustomResponseEntity<>("Invalid carrier or topUp type!");
        }
        Optional<TopUpCustomer> customer = topUpCustomerRepository.findByMobileNumberAndCarrierType(mobileNumber,carrierType);

        if(!customer.isPresent()){
            return new CustomResponseEntity("customer not found !");
        }

        TopUpTransaction transaction = new TopUpTransaction();


        transaction.setCarrierType(carrierType);
        transaction.setAmount(amount);
        transaction.setTopUpType(topUpType);
        transaction.setTopUpCustomer(customer.get());
        transaction.setTransactionDate(new Date());
        TopUpTransaction savedTransaction = topUpTransactionRepository.save(transaction);

        return new CustomResponseEntity(savedTransaction,"Load Details");
    }
}
