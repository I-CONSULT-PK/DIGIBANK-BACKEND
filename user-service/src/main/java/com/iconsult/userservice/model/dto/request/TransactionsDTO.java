package com.iconsult.userservice.model.dto.request;

public class TransactionsDTO {

    private Long id;
    private String bankCode;
    private String transactionId;
    private String transactionDate;
    private Double debitAmt;
    private Double creditAmt;
    private Double currentBalance;
    private String transactionNarration;
    private String accountNumber;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;

    private String customerAddress;

    private String mobileNumber;

    private String accountOpeningDate;

    private String balance;

    private String description;

    private String natureOfAccount;

    private String currency;

    private String ibanCode;



// Getters and Setters

    public TransactionsDTO() {
    }

    public TransactionsDTO(Long id, String bankCode, String transactionId, String transactionDate, Double debitAmt, Double creditAmt, Double currentBalance, String transactionNarration, String accountNumber, String customerFirstName, String customerLastName, String customerEmail) {
        this.id = id;
        this.bankCode = bankCode;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.debitAmt = debitAmt;
        this.creditAmt = creditAmt;
        this.currentBalance = currentBalance;
        this.transactionNarration = transactionNarration;
        this.accountNumber = accountNumber;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerEmail = customerEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Double getDebitAmt() {
        return debitAmt;
    }

    public void setDebitAmt(Double debitAmt) {
        this.debitAmt = debitAmt;
    }

    public Double getCreditAmt() {
        return creditAmt;
    }

    public void setCreditAmt(Double creditAmt) {
        this.creditAmt = creditAmt;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getTransactionNarration() {
        return transactionNarration;
    }

    public void setTransactionNarration(String transactionNarration) {
        this.transactionNarration = transactionNarration;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAccountOpeningDate() {
        return accountOpeningDate;
    }

    public void setAccountOpeningDate(String accountOpeningDate) {
        this.accountOpeningDate = accountOpeningDate;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNatureOfAccount() {
        return natureOfAccount;
    }

    public void setNatureOfAccount(String natureOfAccount) {
        this.natureOfAccount = natureOfAccount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIbanCode() {
        return ibanCode;
    }

    public void setIbanCode(String ibanCode) {
        this.ibanCode = ibanCode;
    }
}
