package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.AccountCDDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AccountCDDetailsRepository extends JpaRepository<AccountCDDetails, Long> {
    AccountCDDetails findByAccount_Id(Long id);
}
