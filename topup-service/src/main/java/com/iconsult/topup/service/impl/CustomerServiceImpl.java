package com.iconsult.topup.service.impl;

import com.iconsult.topup.constants.CarrierType;
import com.iconsult.topup.model.dto.TopUpCustomerRequest;
import com.iconsult.topup.model.entity.TopUpCustomer;
import com.iconsult.topup.repo.TopUpCustomerRepository;
import com.iconsult.topup.service.CustomerService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {
    private  TopUpCustomerRepository topUpCustomerRepository;

    public CustomerServiceImpl (TopUpCustomerRepository topUpCustomerRepository) {
        this.topUpCustomerRepository = topUpCustomerRepository;
    }
    @Override
    public CustomResponseEntity addCustomer(TopUpCustomerRequest request) {

        if (!isValidMobileNumberLength(request.getMobileNumber())) {
            return new CustomResponseEntity ("Mobile number must be 11 digits long");
        }

        if (!isValidMobileNumberForCarrier(request.getMobileNumber(), request.getCarrierType())) {
            return new CustomResponseEntity ("Invalid mobile number for the selected carrier");
        }



        if (topUpCustomerRepository.findByMobileNumber(request.getMobileNumber()).isPresent()) {
          return  new CustomResponseEntity("Customer with this mobile number already exists");
        }


        TopUpCustomer topUpCustomer = new TopUpCustomer();
        topUpCustomer.setName(request.getName());
        topUpCustomer.setCnic(request.getCnic());
        topUpCustomer.setCarrierType(request.getCarrierType());
        topUpCustomer.setMobileNumber(request.getMobileNumber());

        TopUpCustomer savedCustomer = topUpCustomerRepository.save(topUpCustomer);
        return new CustomResponseEntity(savedCustomer, "customer saved");
    }


    private boolean isValidMobileNumberForCarrier(String mobileNumber, CarrierType carrierType) {
        switch (carrierType) {
            case JAZZ:
                return mobileNumber.startsWith("030") || mobileNumber.startsWith("031");
            case UFONE:
                return mobileNumber.startsWith("033");
            case TELENOR:
                return mobileNumber.startsWith("034");
            case ZONG:
                return mobileNumber.startsWith("032");
            default:
                return false;
        }
    }

    private boolean isValidMobileNumberLength(String mobileNumber) {
        return mobileNumber != null && mobileNumber.matches("\\d{11}");
    }
}
