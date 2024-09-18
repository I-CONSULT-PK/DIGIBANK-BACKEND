package com.iconsult.userservice.model.dto.response;

import com.iconsult.userservice.model.entity.Customer;
import lombok.*;

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
    private Customer customer;
    private String ibanCode;
    private String mobileNumber;
//    String bankName;
}
