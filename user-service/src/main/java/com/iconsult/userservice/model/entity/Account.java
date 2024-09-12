package com.iconsult.userservice.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Account")
@JsonIgnoreProperties({"customer", "cardList"})
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean defaultAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties("accountList") // Avoid infinite recursion
    private Customer customer;
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

    @ManyToOne
    @JoinColumn(name = "digi_Branch")
//    @JsonBackReference
    @JsonIgnore
    private DigiBankBranch digiBranch;

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Boolean getDefaultAccount() {
        return defaultAccount;
    }

    public void setDefaultAccount(Boolean defaultAccount) {
        this.defaultAccount = defaultAccount;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Card> cardList;
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

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonBackReference
    private AccountCDDetails accountCdDetails;
    private Double transactionLimit;

    @Column(name = "singleDayLimit")
    private Double singleDayLimit;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", customer=" + customer +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                ", accountType='" + accountType + '\'' +
                ", accountDescription='" + accountDescription + '\'' +
                ", accountOpenDate=" + accountOpenDate +
                ", accountBalance=" + accountBalance +
                ", ibanCode='" + ibanCode + '\'' +
                ", accountClosedDate=" + accountClosedDate +
                ", accountClosedReason='" + accountClosedReason + '\'' +
                ", proofOfIncome='" + proofOfIncome + '\'' +
                ", cardList=" + cardList +
                ", accountCdDetails=" + accountCdDetails +
                ", transactionLimit=" + transactionLimit +
                '}';
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Double getSingleDayLimit() {
        return singleDayLimit;
    }

    public void setSingleDayLimit(Double singleDayLimit) {
        this.singleDayLimit = singleDayLimit;
    }
}
