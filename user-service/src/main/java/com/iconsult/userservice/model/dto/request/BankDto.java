package com.iconsult.userservice.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BankDto {
        private String bankReferenceNumber;
        private String bankName;
        private String bankDesc;
        private String country;
        private String state;
        private String city;
        private Boolean active;
        private List<BranchDto> branches;

    
}
