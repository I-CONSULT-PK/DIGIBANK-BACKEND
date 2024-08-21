package com.zanbeel.otp_service.dto;

import com.zanbeel.otp_service.constants.DeliveryPreference;
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
public class EmailOTPVerifyDto {

    @NotEmpty(message = "Mobile Number is mandatory")
    //@Pattern(regexp =  "^\\+92\\d{10}$", message = "Invalid Mobile number")
    private String mobileNumber;
    @Email
    @NotEmpty
    private String email;
    @NotNull(message = "Email OTP cannot be null")
    @Pattern(regexp = "\\d{4}", message = "Email OTP must be a 4-digit number")
    private String emailOtp;
    private String smsOtp;
    private String reason;

//    @NotNull
    private DeliveryPreference deliveryPreference;
}
