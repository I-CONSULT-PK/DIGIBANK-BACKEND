package com.admin_service.service;

import com.admin_service.model.CustomResponseEntity;

public interface CustomerService {

    CustomResponseEntity getCustomers();

    CustomResponseEntity getActiveCustomers(String action);
}
