package com.iconsult.topup.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TopUpRequest {

    @NotNull
    private String carrierType;

    @NotNull
    private String topUpType;
    @NotNull
    private Double amount;

    @NotNull
    private String mobileNumber;
}
