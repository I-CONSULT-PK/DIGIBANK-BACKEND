package com.iconsult.userservice.model.entity;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chequebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String checkType;

    private String status;

    private LocalDate requestDate;

    private LocalDate cancelledDate;

    private int chequePages;

    @ManyToOne
    @JoinColumn(name = "branch_code", nullable = false)
    private DigiBankBranch branch;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "chequebook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cheque> cheques;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}
