package com.iconsult.userservice.model.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ScheduleMobileTopUpPaymentRequest {

    private Long packageId;

    private String accountNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime localDate;

    private String mobileNumber;

    private Long scheduledId;

    private String status; // optional field
}
