package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Card;
import com.iconsult.userservice.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card,Long> {

    List<Card> getCardByCustomer(Customer id);

}
