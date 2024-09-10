package com.iconsult.topup.model.dto;


import com.iconsult.topup.constants.CarrierType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopUpCustomerRequest {

    private String name;
    private String cnic;
    private CarrierType carrierType;
    private String mobileNumber;
}
