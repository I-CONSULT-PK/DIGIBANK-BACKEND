package com.example.Quartz.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ScheduledTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  bankCode;
//    @ManyToOne
//    @JoinColumn(name = "account_id", nullable = false)
//    private Account account;
//    private String accountNumber;
    private String ibanCode;
    private String transactionId;
    private String transactionDate;
    private Double debitAmt;
    private Double creditAmt;
    private Double currentBalance;
    private String customerOrbitScore;
    private String purposeOfPayment;
    private Double reversalAmt;
    private String revDate;
    private String revId;
    private String revFlag;
    private String privateRailId;
    private String merchantId;
    private String topUpFlag;
    private String govtServiceFlag;
    private String beneficiaryId;
    private String additionalCharges;
    private Double taxAmount;
    private Double serviceCharges;
    private String transactionNarration;
    private String receiverAccount;
    private String senderAccount;
    private String natureOfAccount;
    private String currency;
    private String status;

    public Double getDebitAmt() {
        return debitAmt;
    }

    public void setDebitAmt(Double debitAmt) {
        this.debitAmt = debitAmt;
    }
}
