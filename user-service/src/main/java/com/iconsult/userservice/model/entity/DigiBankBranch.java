package com.iconsult.userservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "digi_branch")
public class DigiBankBranch {

    @Id
    private String branchCode;

    private String branchName;

    private String branchDescription;

    private Date startDate;

    private Date endDate;

    private String region;  // Changed to lowercase for consistency.

    private String country;

    private String state;

    private String city;

    private String branchType;

    private String currencyWiseBase;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @OneToMany(mappedBy = "digiBranch")
    private List<Account> accounts;

}

