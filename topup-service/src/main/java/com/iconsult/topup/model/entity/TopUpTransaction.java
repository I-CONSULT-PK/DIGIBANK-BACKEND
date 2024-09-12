package com.iconsult.topup.model.entity;

import com.iconsult.topup.constants.CarrierType;
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

    private Double amount;

    private Date transactionDate;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private TopUpCustomer topUpCustomer;
    @Enumerated(EnumType.STRING)
    private CarrierType carrierType;

    @Enumerated(EnumType.STRING)
    private TopUpType topUpType;


}
