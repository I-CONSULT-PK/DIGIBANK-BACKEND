package com.iconsult.topup.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MobilePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer validityDays;

    @OneToMany(mappedBy = "mobilePackage")
    private Set<Subscription> subscriptions;
    @ManyToOne
    @JoinColumn(name = "network_id")
    @JsonIgnore
    private Network network;
}
