package com.iconsult.userservice.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FundTransferDto {
    private String receiverAccountNumber;
    private String senderAccountNumber;
    private String bankName;
    @NotNull(message = "Purpose Required")
    private String purpose;
    private String singleDayQRLimit;

    @NotNull(message = "Transaction amount required")
    @DecimalMin(value = "1.0", inclusive = true, message = "Transaction amount must be at least 1")
    private Double transferAmount;
    private String localDate;
    private int successCode;

    public Double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(Double transferAmount) {
        this.transferAmount = transferAmount;
    }
}
