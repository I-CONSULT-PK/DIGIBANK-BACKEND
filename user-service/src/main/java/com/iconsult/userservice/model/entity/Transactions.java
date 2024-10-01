package com.iconsult.userservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transactions implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  bankCode;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
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
    private String transactionType;

    @Column(name = "surrogate_key", unique = true, nullable = false, insertable = false, updatable = false)
    private String surrogateKey;

    @Column(name = "start_date_time", nullable = false, insertable = false, updatable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time",  insertable = false, updatable = false)
    private LocalDateTime endDateTime;

    @Column(name = "trans_status")
    private String status;


    public Double getDebitAmt() {
        return debitAmt;
    }

    public void setDebitAmt(Double debitAmt) {
        this.debitAmt = debitAmt;
    }
}
