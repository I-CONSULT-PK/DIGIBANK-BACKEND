package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.entity.DigiBankBranch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<DigiBankBranch,String> {
}
