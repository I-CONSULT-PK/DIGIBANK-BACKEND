package com.iconsult.userservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchDto {
        private String branchCode;
        private String branchName;
        private String branchDescription;
        private Date startDate;
        private Date endDate;
        private String region;
        private String country;
        private String state;
        private String city;
        private String branchType;
        private String currencyWiseBase;
        private BankDto bankReferenceNumber; // Assuming only bank ID is needed
        private List<AccountDto> accountsNumber; // Assuming only account IDs are needed
    }
