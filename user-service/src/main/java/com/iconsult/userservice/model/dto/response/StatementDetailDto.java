package com.iconsult.userservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StatementDetailDto {

    private String bankCode;
    private String accountNumber;
    private String IBAN;
    private String accountTitle;
    private String registeredAddress;
    private String registeredContact;
    private Date accountOpenDate;
    private String natureOfAccount;
    private String currency;

}
