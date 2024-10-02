package com.iconsult.userservice.feignClient;

import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "add-payeee", url = "http://192.168.0.86:8090/v1/beneficiary") // Replace with the actual service name or URL
public interface BeneficiaryServiceClient {

    @PostMapping("/addTransferAmount")
    public CustomResponseEntity addTransferAmountToBene (@RequestParam String accountNumber , @RequestParam String transferAmount, @RequestParam Long customerId);
}
