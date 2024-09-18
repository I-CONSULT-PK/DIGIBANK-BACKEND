package com.zanbeel.otp_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailOTPSendDto {
    @NotEmpty(message = "Mobile Number is mandatory")
    //@Pattern(regexp =  "^\\+92\\d{10}$", message = "Invalid Mobile number")
    private String mobileNumber;
    @Email
    @NotEmpty
    private String email;
    @NotNull
    private String reason;

    private String deliveryPreference;
}
