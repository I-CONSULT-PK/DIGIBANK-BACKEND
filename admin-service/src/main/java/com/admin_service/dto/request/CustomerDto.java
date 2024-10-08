package com.admin_service.dto.request;
import jakarta.validation.constraints.NotBlank;
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

    String defaultAccountBalance;

    private String accountNumber;

    private String userName;

    private String existingAddress;

    private String newAddress;

    private String city;

    private String province;

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

    public String getDefaultAccountBalance() {
        return defaultAccountBalance;
    }

    public void setDefaultAccountBalance(String defaultAccountBalance) {
        this.defaultAccountBalance = defaultAccountBalance;
    }
}
