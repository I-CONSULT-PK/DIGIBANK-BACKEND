package com.example.AddPayeeService.model.dto;


import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankBranchesDetailDto {

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

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchDescription() {
        return branchDescription;
    }

    public void setBranchDescription(String branchDescription) {
        this.branchDescription = branchDescription;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBranchType() {
        return branchType;
    }

    public void setBranchType(String branchType) {
        this.branchType = branchType;
    }

    public String getCurrencyWiseBase() {
        return currencyWiseBase;
    }

    public void setCurrencyWiseBase(String currencyWiseBase) {
        this.currencyWiseBase = currencyWiseBase;
    }


    public String getBankReferenceNumber() {
        return bankReferenceNumber;
    }

    public void setBankReferenceNumber(String bankReferenceNumber) {
        this.bankReferenceNumber = bankReferenceNumber;
    }

    public List<CbsAccountDto> getAccountsNumber() {
        return accountsNumber;
    }

    public void setAccountsNumber(List<CbsAccountDto> accountsNumber) {
        this.accountsNumber = accountsNumber;
    }
    // Assuming only account IDs are needed
}
