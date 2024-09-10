package com.iconsult.topup.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobilePackageDTO {
    private Long id;
    private String pkg_name;
    private String description;
    private double price;
    private int data_limit;

//    @NotNull
    private Long networkId;
}
