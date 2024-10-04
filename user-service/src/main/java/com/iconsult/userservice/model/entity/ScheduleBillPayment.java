package com.iconsult.userservice.model.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class ScheduleBillPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private   Long Id;

    private String consumerNumber;

    private  String serviceCode;

    private  String utilityType;

    private String accountNumber;

    private String processAmount;

    private String referenceNumber;

    private String billPaymentDate;

    private String status;
}
