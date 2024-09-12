package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.Cheque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChequeRepository extends JpaRepository<Cheque , Long> {
}
