package com.iconsult.topup.model.dto;

import com.iconsult.topup.constants.CarrierType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopUpCustomerDto {
    private String name;
    private String mobileNumber;
    private String email;
    private String CNIC;
    private Date registrationDate;

    private CarrierType carrierType;
}
