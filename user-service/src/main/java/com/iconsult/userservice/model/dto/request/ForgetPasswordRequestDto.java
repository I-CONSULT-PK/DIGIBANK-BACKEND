package com.iconsult.userservice.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgetPasswordRequestDto {
    @NotNull
    private String accountNumber;
    @NotNull
    private String cnic;

    private String password;

//    private Long securityPictureId;
}

