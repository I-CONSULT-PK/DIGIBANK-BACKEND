package com.iconsult.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "userActivity")
public class UserActivity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "userId")
    private String userId;

    @Column(name = "user_Activity")
    private String activity;

    @Column(name = "activity_Date")
    private LocalDateTime activityDate;


}
