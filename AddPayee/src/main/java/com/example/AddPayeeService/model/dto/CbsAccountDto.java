package com.example.AddPayeeService.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;
import java.io.*;

@Data
@AllArgsConstructor
public class CbsAccountDto implements Serializable{

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

    public CbsAccountDto() {

    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountDescription() {
        return accountDescription;
    }

    public void setAccountDescription(String accountDescription) {
        this.accountDescription = accountDescription;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getAccountOpenDate() {
        return accountOpenDate;
    }

    public void setAccountOpenDate(Date accountOpenDate) {
        this.accountOpenDate = accountOpenDate;
    }

    public Date getAccountClosedDate() {
        return accountClosedDate;
    }

    public void setAccountClosedDate(Date accountClosedDate) {
        this.accountClosedDate = accountClosedDate;
    }

    public String getAccountClosedReason() {
        return accountClosedReason;
    }

    public void setAccountClosedReason(String accountClosedReason) {
        this.accountClosedReason = accountClosedReason;
    }

    public String getProofOfIncome() {
        return proofOfIncome;
    }

    public void setProofOfIncome(String proofOfIncome) {
        this.proofOfIncome = proofOfIncome;
    }

}
