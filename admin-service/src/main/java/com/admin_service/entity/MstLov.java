package com.admin_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mst_lov")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MstLov {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String stxt;
    private String ltxt;
    private Integer indx;
    private String vtype;

}
