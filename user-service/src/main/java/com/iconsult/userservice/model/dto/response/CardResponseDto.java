package com.iconsult.userservice.model.dto.response;

import com.iconsult.userservice.model.entity.Card;

import java.util.List;

public class CardResponseDto {

    private Long id;
    private List<Card> cards;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
