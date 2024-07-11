package com.iconsult.userservice.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iconsult.userservice.model.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private Customer customer;
    private Long id;
    //Cbs9
    private String accountNumber;
    private String accountStatus;
    private String accountType;
    private String accountDescription;
    @NonNull
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date accountOpenDate;
    @NonNull
    private Double accountBalance;
    @NonNull
    private String ibanCode;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date accountClosedDate;
    private String accountClosedReason;
    private String proofOfIncome;

    @JsonIgnore
    private BranchDto cbsBranchDto;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public @NonNull Date getAccountOpenDate() {
        return accountOpenDate;
    }

    public void setAccountOpenDate(@NonNull Date accountOpenDate) {
        this.accountOpenDate = accountOpenDate;
    }

    public @NonNull Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(@NonNull Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public @NonNull String getIbanCode() {
        return ibanCode;
    }

    public void setIbanCode(@NonNull String ibanCode) {
        this.ibanCode = ibanCode;
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

    public BranchDto getCbsBranchDto() {
        return cbsBranchDto;
    }

    public void setCbsBranchDto(BranchDto cbsBranchDto) {
        this.cbsBranchDto = cbsBranchDto;
    }
    /*cbs9 done*/
}
