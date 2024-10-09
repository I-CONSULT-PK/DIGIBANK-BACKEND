package com.iconsult.userservice.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iconsult.userservice.model.entity.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class CardDto {

    private Long cardId;
    private Long accountId;
    private String cardHolderName;
    private String accountNumber;
    private String cardNumber;
    private String cvv;

    private String issueDate;
    private String expiryDate;
    private Boolean isActive;

    private String cardType;

    private String pin;
    private Long cid;


    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}

