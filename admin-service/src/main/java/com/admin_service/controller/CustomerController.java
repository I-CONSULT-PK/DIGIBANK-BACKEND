package com.admin_service.controller;

import com.admin_service.model.CustomResponseEntity;
import com.admin_service.service.serviceImpl.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/customer")
public class CustomerController {


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
}
