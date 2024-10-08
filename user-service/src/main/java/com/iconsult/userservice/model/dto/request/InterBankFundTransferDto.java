package com.iconsult.userservice.model.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterBankFundTransferDto {

    private String bankCode;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private Double amount;
    private String purpose;
}
