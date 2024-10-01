package com.admin_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "utdoctypesetup")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UtDocTypeSetup  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String doctypecode;
    private String doctypestxt;

//    @Column(name = "clientid") // Explicitly specify the database column name
    private Integer docTypeClientId;  // Use the same name as the column in the database

    private Date sysdatetime;

    @ManyToOne
    private HdrAdModule modulelid;

//    @OneToMany(mappedBy = "doctypeid")
//    private List<UtDocSubtypeSetup> docSubTypeSetups;

    // Getters and Setters
}

