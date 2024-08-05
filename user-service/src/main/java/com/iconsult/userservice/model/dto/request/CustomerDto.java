package com.iconsult.userservice.model.dto.request;

import com.iconsult.userservice.model.dto.response.AccountDto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CustomerDto {

    private long clientNo;
    @NotBlank(message = "First Name Required")
    private String firstName;
    @NotBlank(message = "Last Name Required")
    private String lastName;
    private GlobalId globalId;
    private String status;
    private Date registeredDate;
    private long internalClientNumber;
    private String categoryType;
    private String branchControl;
    private String countryResident;
    private String stateResident;
    private String residentCity;
    private String address;
    private String mobileNumber;
    @NotBlank(message = "email" +
            " must be required")
    private String email;
    private String clientNature;
    List<AccountDto> accountList;

    private String accountNumber;

    private String userName;

    public long getClientNo() {
        return clientNo;
    }

    public void setClientNo(long clientNo) {
        this.clientNo = clientNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public GlobalId getGlobalId() {
        return globalId;
    }

    public void setGlobalId(GlobalId globalId) {
        this.globalId = globalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public long getInternalClientNumber() {
        return internalClientNumber;
    }

    public void setInternalClientNumber(long internalClientNumber) {
        this.internalClientNumber = internalClientNumber;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getBranchControl() {
        return branchControl;
    }

    public void setBranchControl(String branchControl) {
        this.branchControl = branchControl;
    }

    public String getCountryResident() {
        return countryResident;
    }

    public void setCountryResident(String countryResident) {
        this.countryResident = countryResident;
    }

    public String getStateResident() {
        return stateResident;
    }

    public void setStateResident(String stateResident) {
        this.stateResident = stateResident;
    }

    public String getResidentCity() {
        return residentCity;
    }

    public void setResidentCity(String residentCity) {
        this.residentCity = residentCity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientNature() {
        return clientNature;
    }

    public void setClientNature(String clientNature) {
        this.clientNature = clientNature;
    }

    public List<AccountDto> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<AccountDto> accountList) {
        this.accountList = accountList;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
