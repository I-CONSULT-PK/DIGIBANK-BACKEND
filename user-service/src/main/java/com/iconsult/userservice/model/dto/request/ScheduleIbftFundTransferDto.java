package com.iconsult.userservice.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleIbftFundTransferDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String bankCode;
    @NotNull(message = "Purpose Required")
    private String purpose;

    @NotNull(message = "Transaction amount required")
    @DecimalMin(value = "1.0", inclusive = true, message = "Transaction amount must be at least 1")
    private Double amount;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime localDate;
    private int successCode;
    private Long scheduledId;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
