package com.iconsult.userservice.model.dto.request;

public class TransactionsDTO {
    private String transactionId;
    private String transactionDate;
    private Double debitAmt;
    private Double creditAmt;
    private Double currentBalance;
    private String description;


    public TransactionsDTO(){}

    public TransactionsDTO(String transactionId, String transactionDate, Double debitAmt, Double creditAmt, Double currentBalance, String description) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.debitAmt = debitAmt;
        this.creditAmt = creditAmt;
        this.currentBalance = currentBalance;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
