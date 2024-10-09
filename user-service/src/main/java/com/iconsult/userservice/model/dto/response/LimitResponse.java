package com.iconsult.userservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LimitResponse {


    private Double availedBillPayLimit;
    private Double remainingBillPayLimit;

    private Double availedTopUpLimit;
    private Double remainingTopUpLimit;

    private Double availedOwnLimit;
    private Double remainingOwnLimit;

    private Double availedSendToOtherBankLimit;
    private Double remainingSendToOtherBankLimit;

    private Double availedQRLimit;
    private Double remainingQRLimit;
}
