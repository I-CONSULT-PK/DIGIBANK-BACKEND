package com.iconsult.topup.service;

import com.iconsult.topup.model.dto.TopUpCustomerRequest;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface CustomerService {

    CustomResponseEntity addCustomer (TopUpCustomerRequest request);
}
