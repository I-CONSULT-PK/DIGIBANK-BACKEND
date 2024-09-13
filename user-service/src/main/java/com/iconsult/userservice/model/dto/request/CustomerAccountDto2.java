package com.iconsult.userservice.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerAccountDto2 {
    private String customerName;
    private Double accountBalance;
    private String accountNumber;
    private String ibanCode;
    private String branchName;
    private String branchCode;
}
