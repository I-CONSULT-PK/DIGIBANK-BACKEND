package com.example.AddPayeeService.repository;

import com.example.AddPayeeService.model.entity.AddPayee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddPayeeRepository extends JpaRepository<AddPayee, Long > {

    boolean existsByAccountNumber(String accountNumber);

    AddPayee findByAccountNumberAndCustomerId(String accountNumber, int customerId);

    List<AddPayee> findAllByCustomerId(Long customerId);
    @Query("SELECT a FROM AddPayee a WHERE a.customerId = :customerId AND a.status = :status")
    Optional<List<AddPayee>> findAllByCustomerIdAndStatus(Long customerId, String status);

    Optional<AddPayee> findByCustomerIdAndId(Long customerId , Long Id);

    @Query("SELECT a FROM AddPayee a WHERE a.customerId = :customerId AND a.status = :status AND a.flag = :flag")
    Optional<List<AddPayee>> findAllByCustomerIdAndStatusAndFlag(Long customerId, String status , boolean flag);

    List<AddPayee> findAllByCustomerIdAndFlag(Long customerId , Boolean flag);


}
