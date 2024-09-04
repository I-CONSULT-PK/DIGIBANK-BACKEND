package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Card;
import com.iconsult.userservice.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card,Long> {

    Optional<Card> findByCardNumber(String cardNumber);

}
