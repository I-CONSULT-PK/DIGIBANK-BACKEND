package com.iconsult.userservice.service;



import com.iconsult.userservice.model.dto.request.*;
import com.iconsult.userservice.model.dto.response.SignUpResponse;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.service.Impl.OTPLogImpl;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.servlet.http.HttpSession;
import org.apache.kafka.common.protocol.types.Field;

public interface CustomerService
{

     Customer addUser(Customer customer);

    CustomResponseEntity register(SignUpResponse customerDto, OTPLogImpl otpLogImpl);

    CustomResponseEntity signup(CustomerDto customerDto , OTPLogImpl otpLogImpl);

    CustomResponseEntity signup(SignUpDto signUpDto, OTPLogImpl otpLogImpl);

    Customer findByCnic(String cnic);

    CustomResponseEntity login(LoginDto loginDto);

    void deleteUser(Long id);

    Customer updateCustomer(Customer customer);

    Customer findByEmailAddress(String email);

    Customer findByMobileNumber(String mobileNumber);

    Customer findByUserName(String userName);

    Customer findByResetToken(String resetToken);

    Customer save(Customer customer);

    boolean findBySessionToken(String sessionToken, long currentTime);
    CustomResponseEntity<Customer> findById(Long id);

    CustomResponseEntity forgetUserName(ForgetUsernameDto forgetUsernameDto);
    CustomResponseEntity forgetUserEmail(ForgetUsernameDto forgetUsernameDto);


    CustomResponseEntity verifyCNIC(String cnic, String email , String accountNumber);

    CustomResponseEntity forgetPassword(ForgetUsernameDto forgetUsernameDto);

    CustomResponseEntity forgetPassword(ForgetPasswordRequestDto forgetPasswordRequestDto/*, HttpSession httpSession*/);

    CustomResponseEntity verifyResetPasswordToken(String token);

    CustomResponseEntity resetPassword(ResetPasswordDto resetPasswordDto, HttpSession session);

    public CustomResponseEntity dashboard(Long customerId);

    public CustomResponseEntity setDefaultAccount(Long customerId, String accountNumber,Boolean status);

    public SuggestedUserName generateUniqueUsernames(String userName);


    public CustomResponseEntity fetchUserData(Long Id);

    public CustomResponseEntity getUserAccount(String accountNumber);

    Boolean validateUser(String email, String mobileNumber);

    CustomResponseEntity changeCustomerAddress(Long id, CustomerDto customerDto);
}