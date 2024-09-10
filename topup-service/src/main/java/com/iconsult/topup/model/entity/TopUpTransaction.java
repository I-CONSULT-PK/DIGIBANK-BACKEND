package com.iconsult.topup.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.iconsult.topup.constants.CarrierType;
import com.iconsult.topup.constants.TopUpStatus;
import com.iconsult.topup.constants.TopUpType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TopUpTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TopUpType type;

    @Enumerated(EnumType.STRING)
    private CarrierType carrierType;

    private String mobileNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private TopUpCustomer topUpCustomer;

    private Double amount;

    private Date transactionDate;

    @Enumerated(EnumType.STRING)
    private TopUpStatus status;
}
