package com.example.Quartz.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleFundTransferDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String receiverAccountNumber;
    private String senderAccountNumber;
    private String bankName;
    @NotNull(message = "Purpose Required")
    private String purpose;

    @NotNull(message = "Transaction amount required")
    @DecimalMin(value = "1.0", inclusive = true, message = "Transaction amount must be at least 1")
    private Double transferAmount;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime localDate;
    private int successCode;
    private Long customerId;
    private Long scheduledId;

    public Double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(Double transferAmount) {
        this.transferAmount = transferAmount;
    }
}
