package com.iconsult.topup.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobilePackageDTO {
    private Long id;
    private String pkg_name;
    private String description;
    private double price;
    private Integer validityDays;
    private Long networkId;
}
