package com.example.AddPayeeService.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "Beneficiary")
public class AddPayee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Beneficiary Id")
    private Long id;

    @Column(name = "Beneficiary Name")
    private String beneficiaryName;

    @Column(name = "Beneficiary Alias")
    private String beneficiaryAlias;

    @Column(name = "Account Number")
    private String accountNumber;

    @Column(name = "Account Type")
    private String accountType;

    @Column(name = "Beneficiary Bank Name")
    private String beneficiaryBankName;


    @Column(name = "favourite bene")
    private Boolean flag ;

    @Column(name = "last transfer amount")
    private String lastTransferAmount;

    @Column(name = "Mobile Number")
    private String mobileNumber;

    @Column(name = "Beneficiary Email Id")
    private String beneficiaryEmailId;

    @Column(name = "Status")
    private String status;



    @Column(name = "Category")
    private String categoryType;

    @Column(name = "Category Id")
    private int categoryId;

    @Column(name = "Customer Id")
    private int customerId;
    private String bankUrl;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastTransferAmount() {
        return lastTransferAmount;
    }

    public void setLastTransferAmount(String lastTransferAmount) {
        this.lastTransferAmount = lastTransferAmount;
    }

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

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getBankUrl() {
        return bankUrl;
    }

    public void setBankUrl(String bankUrl) {
        this.bankUrl = bankUrl;
    }
}
