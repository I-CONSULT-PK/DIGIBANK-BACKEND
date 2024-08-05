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
    @JoinColumn(name = "customer_id")
//    @JsonBackReference
    @JsonIgnore
    Customer customer;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "Customer_Number", referencedColumnName = "id") // Defines the foreign key column
////    @JsonIgnore // Prevents infinite recursion in JSON serialization
//    private Customer customer;
    //Cbs9
    @Column(name = "Account_Number")
    private String accountNumber;
    private String accountStatus;
    private String accountType;
    private String accountDescription;


    private Date accountOpenDate;


    private Double accountBalance;


    private String ibanCode;

    private Date accountClosedDate;
    private String accountClosedReason;
    private String proofOfIncome;
//    @ManyToOne
//    @JoinColumn(name = "cbs_Branch")
//    @JsonBackReference
//    @JsonIgnore
//    private Cbs_Branch cbsBranch;


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
