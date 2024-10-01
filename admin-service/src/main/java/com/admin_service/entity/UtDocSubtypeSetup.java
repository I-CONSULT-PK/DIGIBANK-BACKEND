package com.admin_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Entity
@Table(name = "utdocsubtypesetup")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UtDocSubtypeSetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String docsubtypecode;
    private String docsubtypestxt;

    private Integer clientId;

    private Date sysdatetime;

    @ManyToOne
    @JoinColumn(name = "doctypeid")
    private UtDocTypeSetup doctypeid;

    @ManyToOne
    @JoinColumn(name = "modulelid")
    private HdrAdModule modulelid;

    // Uncomment if needed
    // @ManyToOne
    // @JoinColumn(name = "docsubtyperef")
    // private MstLov docsubtyperef;

    // Getters and Setters
}
