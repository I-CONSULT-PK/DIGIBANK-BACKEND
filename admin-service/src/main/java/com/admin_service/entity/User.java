package com.admin_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private Long resetTokenExpireTime;
    private String sessionToken;
    private Long sessionTokenExpireTime;
    private String jobType;
    private Boolean multiTenant;
    private String country;
    private String activation;
    private LocalDate fromDuration;
    private LocalDate toDuration;
    private Boolean status;

    @ManyToMany(fetch = FetchType.EAGER)@JoinTable(name = "user_roles",        joinColumns = @JoinColumn(name = "user_id"),        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
