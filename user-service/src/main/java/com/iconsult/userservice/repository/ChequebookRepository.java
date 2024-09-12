package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.Chequebook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChequebookRepository extends JpaRepository<Chequebook,Long> {

    Optional<Chequebook> findByAccountAndStatus(Account account, String status);

}
