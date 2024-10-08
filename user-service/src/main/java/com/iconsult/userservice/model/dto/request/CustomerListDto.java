package com.iconsult.userservice.model.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerListDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String cnic;
    private String email;
    private String userName;
    private String status; // 00-Active ;; 01-Disable ;; 02-Closed
    private String accountNumber;
    private String registeredAddress;
    private String city;
    private String province;

    // Add any other fields from Customer that you want to expose
}

