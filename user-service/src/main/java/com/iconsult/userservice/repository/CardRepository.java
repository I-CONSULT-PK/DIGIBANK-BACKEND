package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    @Query("SELECT c FROM Account a JOIN a.cardList c WHERE a.accountNumber = :accountNumber")
    List<Card> getCardNumbersAgainstAccountNumber(@Param("accountNumber") String accountNumber);
}
