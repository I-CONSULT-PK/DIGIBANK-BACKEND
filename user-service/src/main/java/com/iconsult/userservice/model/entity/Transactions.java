package com.iconsult.userservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

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
    private Bank Bank;
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    private Customer customer;
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

}
