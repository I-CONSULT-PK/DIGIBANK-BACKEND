package com.iconsult.topup.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MobilePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pkg_name;
    private String description;
    private double price;
    private int data_limit;

//    @ManyToOne
//    @JoinColumn(name = "network_id") // Foreign key column in the MobilePackage table
//    @JsonBackReference // Avoid serializing the back-reference
//    private Network network;

    @ManyToOne
    @JoinColumn(name = "network_id")
    @JsonIgnore // Prevent infinite recursion
    private Network network;

//    // Custom getter for networkId
//    @JsonGetter("networkId")
//    public Long getNetworkId() {
//        return network != null ? network.getId() : null;
//    }
}
