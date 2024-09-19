package com.iconsult.userservice.model.entity;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cheque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String chequeNumber;

    private LocalDate issueDate;

    private LocalDate cancelledDate;

    private String status;

    @ManyToOne
    @JoinColumn(name = "chequebook_id", nullable = false)
    private Chequebook chequebook;

}
