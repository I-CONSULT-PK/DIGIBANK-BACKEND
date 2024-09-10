package com.iconsult.topup.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.iconsult.topup.constants.CarrierType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TopUpCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String cnic;
    @Enumerated(EnumType.STRING)
    private CarrierType carrierType;
    private String mobileNumber;

    @OneToMany(mappedBy = "topUpCustomer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<TopUpTransaction> transactions;

}
