package com.zanbeel.otp_service.config;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "appConfiguration")
@Getter
@Setter
public class AppConfiguration
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String value;
}
