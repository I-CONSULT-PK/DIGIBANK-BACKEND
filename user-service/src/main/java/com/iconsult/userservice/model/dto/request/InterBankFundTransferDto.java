package com.iconsult.userservice.model.dto.request;

import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterBankFundTransferDto {

    private String bankCode;
    private String fromAccountNumberOrIbanCode;
    private String toAccountNumberOrIbanCode;
    private String secretKey;
    private Double amount;
    private Date transactionDate;
    private String purpose;
}
