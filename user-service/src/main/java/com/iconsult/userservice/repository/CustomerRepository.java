package com.iconsult.userservice.repository;

import com.iconsult.userservice.model.dto.request.AccountDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.entity.ImageVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@Transactional
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    Customer findByEmail(String email);
    Customer findByMobileNumber(String mobileNumber);
    Customer findByUserName(String userName);
    Customer findByResetToken(String resetToken);

    Customer findByAccountNumber(String accountNumber);

    @Query("SELECT c FROM Customer c WHERE c.email = :identifier OR c.userName = :identifier OR c.accountNumber = :identifier")
    Customer findByEmailOrUserName(String identifier);

    @Query("SELECT c FROM Customer c WHERE c.cnic = :cnic AND c.accountNumber = :accountNumber")
    Customer findByCnicAndAccountNumber(String cnic , String accountNumber);

    @Query("SELECT c FROM Customer c JOIN c.accountList a WHERE c.cnic = :cnic AND a.accountNumber = :accountNumber")
    Optional<Customer> findCustomerByCnicAndAccountNumber(@Param("cnic") String cnic, @Param("accountNumber") String accountNumber);

    @Query("SELECT c FROM Customer c WHERE c.email = :identifier OR c.userName = :identifier AND c.securityPicture = :securityPicture")
    Customer findByEmailOrUserNameAndSecurityPicture(String identifier, String  securityPicture);

    @Query("SELECT c FROM Customer c WHERE c.accountNumber = :accountNumber  AND c.securityPicture = :securityPicture")
    Customer findByAccountNumberAndSecurityPicture(String accountNumber, String securityPicture);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.sessionToken = :token AND c.sessionTokenExpireTime > :currentTime")
    boolean isValidToken(@Param("token") String token, @Param("currentTime") long currentTime);


    Customer findByCnic(String cnic);
    boolean existsByCnic(String cnic);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByMobileNumber(String mobileNumber);

    Optional<Customer> findByFirstNameAndLastName(String firstName, String lastName);

//    @Query("SELECT u FROM Customer u WHERE u.email = :email AND u.mobileNumber = :mobileNumber")
//    Optional<Customer> findByEmailAndMobileNumber(String email, String mobileNumber);

    Optional<Customer> findByMobileNumberAndEmail(String mobileNumber, String email);


}