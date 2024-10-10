package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.CustomResponseEntity;
import com.iconsult.userservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/branch")
public class BranchController {

    @Autowired
    CustomerService customerService;
    // API to get details of a specific branch
    @GetMapping("/{branchCode}")
    public CustomResponseEntity<?> getBranchDetails(@PathVariable String branchCode) {
        return  new CustomResponseEntity<>(customerService.getBranch(branchCode),"Success");
    }

}
