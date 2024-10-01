package com.admin_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserDto {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private String jobType;
    private Boolean multiTenant;
    private String country;
    private String activation;
    private LocalDate fromDuration;
    private LocalDate toDuration;
}
