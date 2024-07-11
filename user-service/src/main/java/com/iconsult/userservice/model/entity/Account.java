package com.iconsult.userservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Account")
public class Account {
    @ManyToOne
    @JoinColumn(name = "Customer_Number")
//    @JsonBackReference
    @JsonIgnore
    Customer customer;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Cbs9
    @Column(name = "Account_Number")
    private String accountNumber;
    private String accountStatus;
    private String accountType;
    private String accountDescription;
    @NonNull
    private Date accountOpenDate;
    @NonNull
    private Double accountBalance;
    @NonNull
    private String ibanCode;
    private Date accountClosedDate;
    private String accountClosedReason;
    private String proofOfIncome;
//    @ManyToOne
//    @JoinColumn(name = "cbs_Branch")
//    @JsonBackReference
//    @JsonIgnore
//    private Cbs_Branch cbsBranch;

}
