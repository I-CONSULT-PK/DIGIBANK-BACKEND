package com.iconsult.topup.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobilePackageDTO {

    private Long pkgId;
    private String pkgName;
    private String onNetMints;
    private String offNetMints;
    private String smsCount;
    private String totalGBs;
    private String socialGBs;
    private String bundleCategory;
    private Double price;
    private Integer validityDays;
    private Long networkId;
}
