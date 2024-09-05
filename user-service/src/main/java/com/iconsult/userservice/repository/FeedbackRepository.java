package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

//    List<Feedback> findByCustomerId(Long customerId);

    Optional<Feedback> findByCustomerId(Long customerId);
    Optional<Feedback> findByCustomerIdAndTimestampBetween(Long customerId, Date startOfMonth, Date endOfMonth);


    List<Feedback> findAll();
}

