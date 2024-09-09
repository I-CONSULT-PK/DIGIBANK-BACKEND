package com.iconsult.userservice.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iconsult.userservice.constant.PinStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "device")
@Getter
@Setter
public class Device implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToOne
//    @JoinColumn(name = "customer_id", nullable = false)
//    @JsonBackReference
//    @OneToOne
//    @JoinColumn(name = "customer_id")
//    private Customer customer;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private String deviceName;
    private String pinHash;
    private String devicePin;

    private String deviceType;
    private String unique1;
    private String osv_osn;
    private String modelName;
    private String manufacture;

    @Enumerated(EnumType.STRING)
    @Column(name = "pin_status")
    private PinStatus pinStatus = PinStatus.ACTIVE;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
