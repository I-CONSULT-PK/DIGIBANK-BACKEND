package com.iconsult.topup.service.impl;

import com.iconsult.topup.constants.CarrierType;
import com.iconsult.topup.constants.TopUpStatus;
import com.iconsult.topup.constants.TopUpType;
import com.iconsult.topup.model.entity.TopUpCustomer;
import com.iconsult.topup.model.entity.TopUpTransaction;
import com.iconsult.topup.repo.TopUpCustomerRepository;
import com.iconsult.topup.repo.TopUpRepository;
import com.iconsult.topup.service.TopUpService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
public class TopUpServiceImpl implements TopUpService {

    @Autowired
    private TopUpRepository topUpRepository;

    @Autowired
    private  TopUpCustomerRepository topUpCustomerRepository;

    TopUpServiceImpl(){}
    public TopUpServiceImpl (TopUpRepository topUpRepository) {
        this.topUpRepository = topUpRepository;
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
        transaction.setMobileNumber(mobileNumber);
        transaction.setAmount(amount);
        transaction.setType(topUpType);
        transaction.setTopUpCustomer(customer.get());
        transaction.setTransactionDate(new Date());
        transaction.setStatus(TopUpStatus.SUCCESSFUL);
        TopUpTransaction savedTransaction = topUpRepository.save(transaction);

        return new CustomResponseEntity(savedTransaction,"");
    }
}
