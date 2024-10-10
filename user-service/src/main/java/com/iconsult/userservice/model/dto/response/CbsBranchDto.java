package com.iconsult.userservice.model.dto.response;
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
public class CbsBranchDto {

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
//        private Cbs_Bank bankReferenceNumber; // Assuming only bank ID is needed
        private String bankReferenceNumber;
        private List<CbsAccountDto> accountsNumber;

    }
