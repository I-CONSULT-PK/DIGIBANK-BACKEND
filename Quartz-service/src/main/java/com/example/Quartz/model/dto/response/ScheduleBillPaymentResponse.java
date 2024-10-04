package com.example.Quartz.model.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ScheduleBillPaymentResponse {

    private Long id;

    private String consumerNumber;

    private String serviceCode;

    private String utilityType;

    private String accountNumber;

    private String status;
}
