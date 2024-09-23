package com.iconsult.topup.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iconsult.topup.model.entity.Network;
import com.iconsult.topup.model.entity.Subscription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MobilePackageDTO {
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
