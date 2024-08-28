package com.iconsult.userservice.dto;

public class DefaultAccountDto {

    private String firstName;

    private String lastName;

    String defaultAccountBalance;

    private String accountNumber;

    private String accountType;

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

    public String getDefaultAccountBalance() {
        return defaultAccountBalance;
    }

    public void setDefaultAccountBalance(String defaultAccountBalance) {
        this.defaultAccountBalance = defaultAccountBalance;
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
}
