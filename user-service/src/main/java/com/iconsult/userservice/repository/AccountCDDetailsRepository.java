package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.AccountCDDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountCDDetailsRepository extends JpaRepository<AccountCDDetails, Long> {
    AccountCDDetails findByAccount_Id(Long id);
}
