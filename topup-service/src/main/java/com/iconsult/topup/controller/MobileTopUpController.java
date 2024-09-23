package com.iconsult.topup.controller;

import com.iconsult.topup.model.dto.TopUpCustomerRequest;
import com.iconsult.topup.service.CustomerService;
import com.iconsult.topup.service.TopUpService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/topup")
public class MobileTopUpController {


    @Autowired
    private CustomerService customerService;

    @Autowired
    private TopUpService topUpService;
    @PostMapping("/addCustomer")
    public CustomResponseEntity createCustomer(@RequestBody TopUpCustomerRequest request ){
       return customerService.addCustomer(request);
    }

    @PostMapping("/topUpTransaction")
    public CustomResponseEntity transaction (@RequestParam("phoneNumber") String phoneNumber,
                                             @RequestParam("carrier") String carrier,
                                             @RequestParam("amount") Double amount,
                                             @RequestParam("plan") String plan){
        return topUpService.topUpTransaction(phoneNumber,carrier,amount,plan);
    }


}
