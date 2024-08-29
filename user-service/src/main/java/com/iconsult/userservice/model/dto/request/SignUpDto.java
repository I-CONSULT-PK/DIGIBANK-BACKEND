package com.iconsult.userservice.model.dto.request;

import com.iconsult.userservice.model.entity.Device;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
    @NotBlank(message = "mobile is mandatory")
    //@Pattern(regexp = "^\\+923\\d{9}$", message = "Mobile number must follow the pattern +923XXXXXXXXX")
    private String mobileNumber;
    private String firstName;
    private String lastName;
    @NotBlank(message = "cnic is mandatory")
    @Pattern(regexp = "\\d{13}", message = "CNIC must be a 13-digit number")
    private String cnic;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 8, max = 20, message = "Username must be between 8 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must be alphanumeric and without spaces")
    private String userName;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Password must be alphanumeric and without spaces")
    private String password;

    private Long securityPictureId;
    private String resetToken;
    private String status; //00-Active ;; 01-Disable ;; 02-Closed
    private Long resetTokenExpireTime;
    private AccountDto accountDto;

    private Device device;

    public AccountDto getAccountDto() {
        return accountDto;
    }

    public void setAccountDto(AccountDto accountDto) {
        this.accountDto = accountDto;
    }

    public Long getSecurityPictureId() {
        return securityPictureId;
    }

    public void setSecurityPictureId(Long securityPictureId) {
        this.securityPictureId = securityPictureId;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
