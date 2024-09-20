package com.iconsult.topup.service.Impl;

import com.iconsult.topup.constants.CarrierType;
import com.iconsult.topup.model.dto.TopUpCustomerDto;
import com.iconsult.topup.model.dto.TopUpCustomerRequest;
import com.iconsult.topup.model.entity.TopUpCustomer;
import com.iconsult.topup.repo.TopUpCustomerRepository;
import com.iconsult.topup.service.CustomerService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private TopUpCustomerRepository topUpCustomerRepository;

    public CustomerServiceImpl(TopUpCustomerRepository topUpCustomerRepository) {
        this.topUpCustomerRepository = topUpCustomerRepository;
    }

    @Override
    public CustomResponseEntity addCustomer(TopUpCustomerRequest request) {

        Optional<TopUpCustomer> customerExists = topUpCustomerRepository.findByEmail(request.getEmail());

        if (customerExists.isPresent()) {
            return CustomResponseEntity.error("customer with this email already registered : " + request.getEmail());
        }

        if (topUpCustomerRepository.findByMobileNumber(request.getMobileNumber()).isPresent()) {
            return CustomResponseEntity.error("Customer with this mobile number already exists : " + request.getMobileNumber());
        }

        if (topUpCustomerRepository.findByCnic(request.getCnic()).isPresent()) {
            return CustomResponseEntity.error("Customer with this cnic already exists : " + request.getCnic());
        }


        if (!isValidMobileNumberLength(request.getMobileNumber())) {
            return CustomResponseEntity.error("Mobile number must be 11 digits long");
        }

        if (!isValidMobileNumberForCarrier(request.getMobileNumber(), request.getCarrierType())) {
            return CustomResponseEntity.error("Invalid mobile number for the selected carrier");
        }

        TopUpCustomer topUpCustomer = new TopUpCustomer();
        topUpCustomer.setName(request.getName());
        topUpCustomer.setCNIC(request.getCnic());
        topUpCustomer.setEmail(request.getEmail());
        topUpCustomer.setMobileNumber(request.getMobileNumber());
        topUpCustomer.setRegistrationDate(new Date());
        topUpCustomer.setCarrierType(request.getCarrierType());
        topUpCustomerRepository.save(topUpCustomer);

        TopUpCustomerDto customerDto = customerJpeToDto(topUpCustomer);
        return new CustomResponseEntity(customerDto, "customer saved");
    }

    TopUpCustomerDto customerJpeToDto(TopUpCustomer customer) {

        TopUpCustomerDto dto = new TopUpCustomerDto();
        dto.setName(customer.getName());
        dto.setCNIC(customer.getCNIC());
        dto.setEmail(customer.getEmail());
        dto.setRegistrationDate(customer.getRegistrationDate());
        dto.setMobileNumber(customer.getMobileNumber());
        dto.setCarrierType(customer.getCarrierType());
        return dto;
    }

    private boolean isValidMobileNumberForCarrier(String mobileNumber, CarrierType carrierType) {
        switch (carrierType) {
            case JAZZ:
                return mobileNumber.startsWith("030") || mobileNumber.startsWith("032");
            case UFONE:
                return mobileNumber.startsWith("033");
            case TELENOR:
                return mobileNumber.startsWith("034");
            case ZONG:
                return mobileNumber.startsWith("031");
            default:
                return false;
        }
    }

    private boolean isValidMobileNumberLength(String mobileNumber) {
        return mobileNumber != null && mobileNumber.matches("\\d{11}");
    }
}
