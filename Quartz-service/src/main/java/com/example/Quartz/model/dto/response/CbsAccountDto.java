package com.example.Quartz.model.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CbsAccountDto {
    private Double lastCredit;
    private Double lastDebit;
    private String cnicNo;
    private Long id;
    private String accountNumber;
    private String accountTitle;
    private String accountStatus;
    private String accountType;
    private String accountDescription;
    private String email;
    private Date accountOpenDate;
    private Double accountBalance;
    private Date accountClosedDate;
    private String accountClosedReason;
    private String proofOfIncome;
    private String branchCode;
//    private Customer customer;
    private String ibanCode;
//    String bankName;
}
