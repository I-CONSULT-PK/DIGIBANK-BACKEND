package com.iconsult.userservice.dto;

import com.iconsult.userservice.model.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityRequest {


    private Long id;

    private Customer customerId;
    private String userActivity;


    private LocalDateTime activityDate;
}
