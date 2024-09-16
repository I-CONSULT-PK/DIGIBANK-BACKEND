package com.iconsult.userservice.dto;

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

    private String userId;


    private String userActivity;


    private LocalDateTime activityDate;
}
