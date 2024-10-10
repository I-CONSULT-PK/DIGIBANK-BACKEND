package com.example.Quartz.model.entity;


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
public class ScheduleMobileTopUpPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private   Long Id;

    private Long packageId;

    private  String accountNumber;

    private String mobileNumber;

    private String mobileTopUpPaymentDate;

    private String status;


}
