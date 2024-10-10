package com.admin_service.controller;

import com.admin_service.enumeration.TimePeriod;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.service.serviceImpl.AdminServiceImpl;
import com.admin_service.service.serviceImpl.CustomerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/customer")
public class CustomerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerServiceImpl customerService;

    @GetMapping("/getCustomers")
    public CustomResponseEntity getCustomers() {
       return this.customerService.getCustomers();
    }

    @GetMapping("/getActiveCustomers")
    public CustomResponseEntity getActiveCustomers(@RequestParam("action")  String action) {
        return this.customerService.getActiveCustomers(action);
    }
    @GetMapping("/getTotalCreditDebit")
    public CustomResponseEntity getTotalCreditDebit(@RequestParam String record) {
           return customerService.getTotalCreditDebit(record);
    }
}
