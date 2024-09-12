package com.iconsult.userservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TopPackageTransactionDto {
    private String accountNumber;
    private Double debitAmount;
    private Double creditAmount;
    private Double currentBalance;
    private String transactionId;
    private String transactionDate;
    private String ibanCode;
    private String transactionNarration;
}
