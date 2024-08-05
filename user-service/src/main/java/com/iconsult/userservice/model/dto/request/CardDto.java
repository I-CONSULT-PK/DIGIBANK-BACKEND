package com.iconsult.userservice.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iconsult.userservice.model.entity.Customer;
import jakarta.persistence.*;

import java.util.Date;

public class CardDto {

        private Long cardId;
        private Long cid;

        private String cardNumber;

        private String cvv;
        private String expiryDate;

        private String cardHolderName;

        private Boolean isActive;



    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "CardDto{" +
                "cardId=" + cardId +
                ", cid=" + cid +
                ", cardNumber='" + cardNumber + '\'' +
                ", cvv='" + cvv + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

