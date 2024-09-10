package com.iconsult.topup.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NetworkDTO {
    private Long id;
    private String name;
    private List<MobilePackageDTO> mobilePackages;
}

