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
    private String fromAccountNumber;
    private String toAccountNumber;
    private Double amount;
    private String purpose;
}
