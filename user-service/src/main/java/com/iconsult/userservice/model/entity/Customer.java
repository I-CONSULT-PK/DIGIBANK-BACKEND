package com.iconsult.userservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Mobile number cannot be null")
    @Pattern(regexp = "^\\+923[0-9]{9}$", message = "Mobile number must be in the format +923XXXXXXXXX")
    private String mobileNumber;
    private String firstName;
    private String lastName;
    private String cnic;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
    private String userName;
    private String password;
    private String securityPicture;
    private String resetToken;
    private String status; //00-Active ;; 01-Disable ;; 02-Closed
    private Long resetTokenExpireTime;
    private String sessionToken;
    private Long sessionTokenExpireTime;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Long getSessionTokenExpireTime() {
        return sessionTokenExpireTime;
    }

    public void setSessionTokenExpireTime(Long sessionTokenExpireTime) {
        this.sessionTokenExpireTime = sessionTokenExpireTime;
    }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
//            @JsonManagedReference
    List<Account> accountList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityPicture() {
        return securityPicture;
    }

    public void setSecurityPicture(String securityPicture) {
        this.securityPicture = securityPicture;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getResetTokenExpireTime() {
        return resetTokenExpireTime;
    }

    public void setResetTokenExpireTime(Long resetTokenExpireTime) {
        this.resetTokenExpireTime = resetTokenExpireTime;
    }
}
