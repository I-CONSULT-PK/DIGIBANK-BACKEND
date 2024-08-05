package com.iconsult.userservice.model.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgetUserAndPasswordResponse {

    String email;

    String mobileNumber;
}
