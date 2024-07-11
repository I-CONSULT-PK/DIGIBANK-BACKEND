package com.iconsult.userservice.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
    @NotBlank(message = "mobile is mandatory")
    @Pattern(regexp = "^\\+923\\d{9}$", message = "Mobile number must follow the pattern +923XXXXXXXXX")
    private String mobileNumber;
    private String firstName;
    private String lastName;
    @NotBlank(message = "cnic is mandatory")
    private String cnic;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
    private String userName;
    private String password;
    private String securityPicture;
    private String resetToken;
    private String status; //00-Active ;; 01-Disable ;; 02-Closed
    private Long resetTokenExpireTime;
    private String accountNumber;
}
