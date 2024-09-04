package com.iconsult.userservice.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailOTPSendDto {
    @NotEmpty(message = "Mobile Number is mandatory")
    private String mobileNumber;
    @Email
    @NotEmpty
    private String email;
    @NotNull
    private String reason;

}
