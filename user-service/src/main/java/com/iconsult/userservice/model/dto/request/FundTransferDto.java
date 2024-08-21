package com.iconsult.userservice.model.dto.request;

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
    private String purpose;
    private Double transferAmount;
    private String localDate;
    private int successCode;


}
