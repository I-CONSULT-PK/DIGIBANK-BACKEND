package com.example.AddPayeeService.model.dto.response;

import java.io.Serializable;

import com.example.AddPayeeService.model.entity.AddPayee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AddPayeeResponseDto  {
    private Long id;
    private String beneficiaryName;
    private String beneficiaryAlias;
    private String accountNumber;
    private String accountType;
    private String beneficiaryBankName;
    private String beneficiaryAddress1;
    private String beneficiaryAddress2;
    private String beneficiaryBankAddress;
    private String mobileNumber;
    private String beneficiaryEmailId;
    private String status;
    private int shelfId;
    private String categoryType;
    private int categoryId;
    private String bankUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryAlias() {
        return beneficiaryAlias;
    }

    public void setBeneficiaryAlias(String beneficiaryAlias) {
        this.beneficiaryAlias = beneficiaryAlias;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBeneficiaryBankName() {
        return beneficiaryBankName;
    }

    public void setBeneficiaryBankName(String beneficiaryBankName) {
        this.beneficiaryBankName = beneficiaryBankName;
    }

    public String getBeneficiaryAddress1() {
        return beneficiaryAddress1;
    }

    public void setBeneficiaryAddress1(String beneficiaryAddress1) {
        this.beneficiaryAddress1 = beneficiaryAddress1;
    }

    public String getBeneficiaryAddress2() {
        return beneficiaryAddress2;
    }

    public void setBeneficiaryAddress2(String beneficiaryAddress2) {
        this.beneficiaryAddress2 = beneficiaryAddress2;
    }

    public String getBeneficiaryBankAddress() {
        return beneficiaryBankAddress;
    }

    public void setBeneficiaryBankAddress(String beneficiaryBankAddress) {
        this.beneficiaryBankAddress = beneficiaryBankAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getBeneficiaryEmailId() {
        return beneficiaryEmailId;
    }

    public void setBeneficiaryEmailId(String beneficiaryEmailId) {
        this.beneficiaryEmailId = beneficiaryEmailId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getShelfId() {
        return shelfId;
    }

    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getBankUrl() {
        return bankUrl;
    }

    public void setBankUrl(String bankUrl) {
        this.bankUrl = bankUrl;
    }
}
