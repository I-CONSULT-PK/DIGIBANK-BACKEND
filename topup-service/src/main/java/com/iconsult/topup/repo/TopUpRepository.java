package com.iconsult.topup.repo;

import com.iconsult.topup.model.entity.TopUpTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopUpRepository extends JpaRepository<TopUpTransaction,Long> {
}
