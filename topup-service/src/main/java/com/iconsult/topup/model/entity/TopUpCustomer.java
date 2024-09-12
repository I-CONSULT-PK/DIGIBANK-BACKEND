package com.iconsult.topup.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.iconsult.topup.constants.CarrierType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
    private String mobileNumber;
    private String email;
    private String CNIC;
    private Date registrationDate;
    @OneToMany(mappedBy = "topUpCustomer", cascade = CascadeType.ALL)
    private Set<Subscription> subscriptions;

    @OneToMany(mappedBy = "topUpCustomer")
    private Set<TopUpTransaction> transactions;
    @Enumerated(EnumType.STRING)
    private CarrierType carrierType;

}
