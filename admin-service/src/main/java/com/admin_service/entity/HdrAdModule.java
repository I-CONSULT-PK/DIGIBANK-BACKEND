package com.admin_service.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "hdr_admodule")
@AllArgsConstructor
@Getter
@Setter
public class HdrAdModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String moduleCode;
    private String moduleTxt;
    private String modulesTxt;
    private String moduleLtxt;

    private Integer clientId;
    private Date sysDateTime;

    @OneToMany
    private List<UtDocTypeSetup> docTypeSetups;

    public HdrAdModule() {}

    // Getters and Setters
}


