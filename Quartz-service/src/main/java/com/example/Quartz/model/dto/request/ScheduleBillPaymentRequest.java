package com.example.Quartz.model.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ScheduleBillPaymentRequest implements Serializable {

    private String consumerNumber;

    private String serviceCode;

    private String utilityType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime localDate;

    private Long scheduledId;

    private String accountNumber;
}
