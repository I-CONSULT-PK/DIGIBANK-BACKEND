package com.example.AddPayeeService.model.dto;

import lombok.*;

import java.util.List;
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BanksDto {
    private String bankReferenceNumber;
    private String bankName;
    private String bankDesc;
    private String country;
    private String state;
    private String city;
    private Boolean active;
    private List<BankBranchesDetailDto> branches;


}
