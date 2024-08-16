package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.Util.Util;
import com.iconsult.userservice.domain.OTP;
import com.iconsult.userservice.dto.EmailDto;
import com.iconsult.userservice.enums.CustomerStatus;
import com.iconsult.userservice.enums.ResponseCodes;
import com.iconsult.userservice.exception.ServiceException;
import com.iconsult.userservice.model.dto.request.*;
import com.iconsult.userservice.model.dto.response.DashBoardResponseDto;
import com.iconsult.userservice.model.dto.response.ForgetUserAndPasswordResponse;
import com.iconsult.userservice.model.dto.response.KafkaMessageDto;
import com.iconsult.userservice.model.dto.response.SignUpResponse;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.AppConfiguration;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.entity.ImageVerification;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.repository.ImageVerificationRepository;
import com.iconsult.userservice.service.CustomerService;
import com.iconsult.userservice.service.EmailService;
import com.iconsult.userservice.service.JwtService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CustomerServiceImpl implements CustomerService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    //private final String URL = "http://192.168.0.196:8085/account/getAccounts?cnicNumber=%s";
//    private final String URL = "http://localhost:8081/customer/getDetails?cnicNumber=%s&";
    //  private final String URL = "http://localhost:8081/customer/getDetails?cnicNumber=%s&mobileNumber=%s&accountNumber=%s";
    //  private final String URL =   "http://localhost:8082/customer/get/cnic/mobileNumber/accountNumber?cnicNumber=%s&mobileNumber=%s&accountNumber=%s";

    private final String URL = "http://localhost:8081/customer/get/cnic/mobileNumber/accountNumber";
    private final String dashBoardCBSURL = "http://192.168.0.153:8081/account/dashboard";
    private final String setdefaultaccountCBSURL = "http://192.168.0.153:8081/customer/setdefaultaccount";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    private KafkaMessageDto kafkaMessage;

    private CustomResponseEntity response;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccountRepository accountRepository;

//    @Autowired
//    private CustomerMapperImpl customerMapperImpl;

    //    @Autowired
//    private final PasswordEncoder passwordEncoder;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppConfigurationImpl appConfigurationImpl;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;
//    @Autowired
//    private EmailService emailService;

    @Autowired
    private ImageVerificationRepository imageVerificationRepository;

    @Autowired
    AccountServiceImpl accountService;

    //String deliveryPreference = DeliveryPreference.EMAIL.getValue();
//    @Autowired
//    public CustomerServiceImpl(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }

//    @Override
//    public CustomResponseEntity register(CustomerDto customerDto)
//    {
//        LOGGER.info("Sign up Request received");
//
//        // Duplicate Customer Check mobile number
//        Customer customerDuplicate = findByMobileNumber(customerDto.getMobileNumber());
//
//        if(customerDuplicate != null)
//        {
//            LOGGER.error("Customer already exists with mobile [" + customerDto.getMobileNumber() + "], cannot allow signup, rejecting...");
//            throw new ServiceException(String.format("Customer with Mobile Number %s already exists", customerDto.getMobileNumber()));
//        }
//
//        // Duplicate Customer Check if email is present
//        if(!customerDto.getEmail().isBlank())
//        {
//            customerDuplicate = findByEmailAddress(customerDto.getEmail());
//            if(customerDuplicate != null)
//            {
//                LOGGER.error("Customer already exists with Email [" + customerDto.getEmail() + "], cannot allow signup, rejecting...");
//                throw new ServiceException(String.format("Customer with Email %s already exists", customerDto.getEmail()));
//            }
//        }
//
//        // Duplicate Customer Check username
//        customerDuplicate = findByUserName(customerDto.getUserName());
//
//        if(customerDuplicate != null)
//        {
//            LOGGER.error("Customer already exists with userName [" + customerDto.getUserName() + "], cannot allow signup, rejecting...");
//            throw new ServiceException(String.format("Customer with userName %s already exists", customerDto.getUserName()));
//        }
//
//        Customer customer = addUser(customerMapperImpl.dtoToJpe(customerDto));
//
//        LOGGER.info("Customer has been saved with Id {}", customer.getId());
//        Map<String,Object> result = new HashMap<>();
//        result.put("mobileNumber",customer.getMobileNumber());
//        result.put("customerId",customer.getId());
//
//        return response;
//    }

//    @Override
//    public CustomResponseEntity register(CustomerDto customerDto, OTPLogImpl otpLogImpl)
//    {
//        LOGGER.info("Sign up Request received");
//        CustomerDto existingCustomer = accountExist(customerDto.getCnic(), customerDto.getMobileNumber(), customerDto.getAccountNumber());
//
//        if(existingCustomer != null)
//        {
//            LOGGER.error("Customer account does not exist [" + customerDto.getCnic() + "], cannot allow signup, rejecting...");
//            throw new ServiceException(String.format("Customer account [%s] does not exist", customerDto.getCnic()));
//        }
//
//        // Duplicate Customer Check mobile number
//        Customer customerDuplicate = findByMobileNumber(customerDto.getMobileNumber());
//
//        if(customerDuplicate != null)
//        {
//            LOGGER.error("Customer already exists with mobile [" + customerDto.getMobileNumber() + "], cannot allow signup, rejecting...");
//            throw new ServiceException(String.format("Customer with Mobile Number %s already exists", customerDto.getMobileNumber()));
//        }
//
//        // Duplicate Customer Check if email is present
//        if(!customerDto.getEmail().isBlank())
//        {
//            customerDuplicate = findByEmailAddress(customerDto.getEmail());
//            if(customerDuplicate != null)
//            {
//                LOGGER.error("Customer already exists with Email [" + customerDto.getEmail() + "], cannot allow signup, rejecting...");
//                throw new ServiceException(String.format("Customer with Email %s already exists", customerDto.getEmail()));
//            }
//        }
//
//        // Duplicate Customer Check username
//        customerDuplicate = findByUserName(customerDto.getUserName());
//
//        if(customerDuplicate != null)
//        {
//            LOGGER.error("Customer already exists with userName [" + customerDto.getUserName() + "], cannot allow signup, rejecting...");
//            throw new ServiceException(String.format("Customer with userName %s already exists", customerDto.getUserName()));
//        }
//
////        Customer customer = customerMapperImpl.dtoToJpe(customerDto);
////        customer.setStatus(CustomerStatus.TEMP_BLOCK.getCode());
////        addUser(customer);
//
//        //LOGGER.info("Customer has been saved with Id {}", customer.getId());
//
//        if(!otpLogImpl.createAndSendOTP(new OTPDto(customerDto.getMobileNumber(), customerDto.getEmail(), SMSCategory.VERIFY_MOBILE_DEVICE.getValue()))) // sending OTP after register
//        {
//            LOGGER.error("Failed to create & Send OTP for Mobile [" + customerDto.getMobileNumber() + "], rejecting...");
//            throw new ServiceException("SMS Gateway Down");
//        }
//
////        Map<String,Object> result = new HashMap<>();
////        result.put("mobileNumber",customer.getMobileNumber());
////        result.put("customerId",customer.getId());
//
//        response = new CustomResponseEntity<>("OTP sent Successfully");
//
//        return response;
//    }

//    @Override
//    public CustomResponseEntity signup(CustomerDto customerDto, OTPLogImpl otpLogImpl) {
//        Customer customer = customerMapperImpl.dtoToJpe(customerDto);
//
//        if(!otpLogImpl.createAndSendOTP(new OTPDto(customerDto.getEmail()))){
//            LOGGER.error("Failed to create & Send OTP to email [" + customerDto.getEmail() + "], rejecting...");
//            throw new ServiceException("SMS Gateway Down");
//        }
//        customerRepository.save(customer);
//        return new CustomResponseEntity<>("customer saved succesfully");
//    }

    public CustomResponseEntity register(SignUpResponse customerDto, OTPLogImpl otpLogImpl) {
        LOGGER.info("Sign up Request received");

        // Check if customer account exists
        SignUpResponse existingCustomer = accountExist(customerDto.getGlobalId().getCnicNumber(), customerDto.getCustomer().getMobileNumber(), customerDto.getAccount().getAccountNumber());

        if (existingCustomer == null) {
            LOGGER.error("Customer account does not exist [" + customerDto.getGlobalId().getCnicNumber() + "], cannot allow signup, rejecting...");
            throw new ServiceException(String.format("Customer account [%s] does not exist", customerDto.getGlobalId().getCnicNumber()));
        }

        // Duplicate Customer Check mobile number
        Customer customerDuplicate = customerRepository.findByMobileNumber(customerDto.getCustomer().getMobileNumber());

        if (customerDuplicate != null) {
            LOGGER.error("Customer already exists with mobile [" + customerDto.getCustomer().getMobileNumber() + "], cannot allow signup, rejecting...");
            throw new ServiceException(String.format("Customer with Mobile Number %s already exists", customerDto.getCustomer().getMobileNumber()));
        }

        // Duplicate Customer Check if email is present
//        if (!customerDto.getCustomer().getEmail().isBlank()) {
//            customerDuplicate = findByEmailAddress(customerDto.getCustomer().getEmail());
//            if (customerDuplicate != null) {
//                LOGGER.error("Customer already exists with Email [" + customerDto.getCustomer().getEmail() + "], cannot allow signup, rejecting...");
//                throw new ServiceException(String.format("Customer with Email %s already exists", customerDto.getCustomer().getEmail()));
//            }
//        }

        // Duplicate Customer Check username
//        customerDuplicate = findByCnic(customerDto.getGlobalId().getCnicNumber());
//
//        if (customerDuplicate != null) {
//            LOGGER.error("Customer already exists with userName [" + customerDto.getGlobalId().getCnicNumber() + "], cannot allow signup, rejecting...");
//            throw new ServiceException(String.format("Customer with cnic %s already exists", customerDto.getGlobalId().getCnicNumber()));
//        }

        Account accountByAccountNumber = accountRepository.getAccountByAccountNumber(customerDto.getAccount().getAccountNumber());

        if (accountByAccountNumber != null) {
            LOGGER.error("Customer already exists with userName [" + customerDto.getAccount().getAccountNumber() + "], cannot allow signup, rejecting...");
            throw new ServiceException(String.format("Customer with accountNumber %s already exists", customerDto.getAccount().getAccountNumber()));
        }



        // Create and send OTP (uncomment this section if needed)
//    if (!otpLogImpl.createAndSendOTP(new OTPDto(customerDto.getCustomer().getMobileNumber(), customerDto.getCustomer().getEmail(), SMSCategory.VERIFY_MOBILE_DEVICE.getValue()))) {
//        LOGGER.error("Failed to create & Send OTP for Mobile [" + customerDto.getCustomer().getMobileNumber() + "], rejecting...");
//        throw new ServiceException("SMS Gateway Down");
//    }

        LOGGER.info("OTP sent Successfully");

        return new CustomResponseEntity(existingCustomer, "Response Sent");
    }

    @Override
    public CustomResponseEntity signup(CustomerDto customerDto, OTPLogImpl otpLogImpl) {
        return null;
    }


    @Override
    @Transactional
    public CustomResponseEntity signup(SignUpDto signUpDto, OTPLogImpl otpLogImpl) {
        // Check if the signUpDto is null
        if (signUpDto == null) {
            throw new ServiceException("SignUp data cannot be null");
        }

        try {
            // Check if CNIC, email, username, or mobile number already exist
            boolean cnicExists = customerRepository.existsByCnic(signUpDto.getCnic());
            boolean emailExists = customerRepository.existsByEmail(signUpDto.getEmail());
            boolean userNameExists = customerRepository.existsByUserName(signUpDto.getUserName());
            boolean mobileNumberExists = customerRepository.existsByMobileNumber(signUpDto.getMobileNumber());

            if (cnicExists) {
                throw new ServiceException("An account with this CNIC already exists");
            }
            if (emailExists) {
                throw new ServiceException("An account with this email already exists");
            }
            if (userNameExists) {
                throw new ServiceException("An account with this username already exists");
            }
            if (mobileNumberExists) {
                throw new ServiceException("An account with this mobile number already exists");
            }

            // Validate the security picture ID
            ImageVerification imageVerification = imageVerificationRepository.findById(signUpDto.getSecurityPictureId())
                    .orElseThrow(() -> new ServiceException("Image does not exist"));

            // Validate the account information in signUpDto
            AccountDto accountDto = signUpDto.getAccountDto();
            if (accountDto == null || accountDto.getAccountNumber() == null || accountDto.getAccountNumber().isBlank()) {
                throw new ServiceException("Account number should not be null or empty");
            }

            // Create a customer entity from the DTO
            Customer customer = new Customer();
            customer.setMobileNumber(signUpDto.getMobileNumber());
            customer.setFirstName(signUpDto.getFirstName());
            customer.setLastName(signUpDto.getLastName());
            customer.setCnic(signUpDto.getCnic());
            customer.setEmail(signUpDto.getEmail());
            customer.setUserName(signUpDto.getUserName());
            customer.setPassword(signUpDto.getPassword());
            customer.setSecurityPicture(imageVerification.getName());
            customer.setStatus("00");
            customer.setStatus(signUpDto.getStatus());
            customer.setResetToken(signUpDto.getResetToken());
            customer.setResetTokenExpireTime(signUpDto.getResetTokenExpireTime());

            // Initialize the account list if null
            if (customer.getAccountList() == null) {
                customer.setAccountList(new ArrayList<>());
            }

            // Check for duplicate accounts before adding
            for (Account existingAccount : customer.getAccountList()) {
                if (existingAccount.getAccountNumber().equals(accountDto.getAccountNumber())) {
                    throw new ServiceException("Duplicate account number detected. The customer already has an account with this number.");
                }
            }

            // Create the account entity and set its properties
            Account account = new Account();
            account.setAccountNumber(accountDto.getAccountNumber());
            account.setAccountBalance(0.0);
            account.setAccountType(accountDto.getAccountType());
            account.setAccountDescription(accountDto.getAccountDescription());
            account.setAccountStatus(accountDto.getAccountStatus());
            account.setIbanCode(accountDto.getIbanCode());
            account.setAccountOpenDate(new Date());
            account.setProofOfIncome(accountDto.getProofOfIncome());
            account.setCustomer(customer); // Set the customer reference in the account

            // Add the account to the customer's account list
            customer.getAccountList().add(account);

            // Save the customer entity, which will cascade and save the account
            customerRepository.save(customer);

            // Save the account entity
            accountService.createAccount(signUpDto.getAccountDto());

            return new CustomResponseEntity<>("Customer Registered successfully");

        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer findByCnic(String cnic) {
        return customerRepository.findByCnic(cnic);
    }


    @Override
    public CustomResponseEntity login(LoginDto loginDto) {
        ImageVerification imageVerification = null;
        if(loginDto.getImageVerificationId() != null) {
            imageVerification = imageVerificationRepository.findById(loginDto.getImageVerificationId()).orElse(null);
        }
        Customer customer;
        if(imageVerification == null) {
            customer = customerRepository.findByEmailOrUserName(loginDto.getEmailorUsername());
        }else {
            customer = customerRepository.findByEmailOrUserNameAndSecurityPicture(loginDto.getEmailorUsername(),imageVerification.getName());
        }
        if (customer != null) {
            if (!customer.getStatus().equals(CustomerStatus.ACTIVE.getCode())) // customer not active
            {
                response = new CustomResponseEntity<>(ResponseCodes.CUSTOMER_INACTIVE.getCode(), ResponseCodes.CUSTOMER_INACTIVE.getValue());
                return response;
            }

            if (customer.getPassword().equals(loginDto.getPassword()) &&
                    (loginDto.getSecurityImage() == null || customer.getSecurityPicture().equals(loginDto.getSecurityImage()))) {
                // JWT Implementation Starts
                Authentication authentication =
                        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmailorUsername(), loginDto.getPassword()));
                String email = authentication.getName();
                String token = jwtService.generateToken(email);
                LOGGER.info("Token = " + token);
                LOGGER.info("Expiration = " + jwtService.getTokenExpireTime(token).getTime());
                // JWT Implementation Ends

                Map<String, Object> data = new HashMap<>();
                data.put("customerId", customer.getId());
                data.put("token", token);
                data.put("expirationTime", jwtService.getTokenExpireTime(token).getTime());
                response = new CustomResponseEntity<>(data, "customer logged in successfully");

                //set customer token
                customer.setSessionToken(token);
                AppConfiguration appConfiguration = this.appConfigurationImpl.findByName("RESET_EXPIRE_TIME"); // fetching token expire time in minutes
                customer.setSessionTokenExpireTime(Long.parseLong(Util.dateFormat.format(DateUtils.addMinutes(new Date(), Integer.parseInt(appConfiguration.getValue())))));
                updateCustomer(customer);
                return response;
            } else {
                throw new ServiceException("Invalid Password or Security Image");
            }
        }

        throw new ServiceException("Customer does not exist");
    }

    @Override
    public CustomResponseEntity verifyCNIC(String cnic, String mobileNumber, String accountNumber) {
        LOGGER.info("Verify CNIC request received");

        SignUpResponse existingCustomer = accountExist(cnic, mobileNumber, accountNumber);

        if (existingCustomer != null) {
            LOGGER.error("Customer account does not exist [" + cnic + "], cannot allow signup, rejecting...");
            throw new ServiceException(String.format("Customer account does not exists", cnic));
        }
        return new CustomResponseEntity<>("Customer Account Exists, allowing sign up");
    }

    @Override
    public CustomResponseEntity forgetPassword(ForgetUsernameDto forgetUsernameDto) {
        return null;
    }


    @Override
    public Customer addUser(Customer customer) {
        return save(customer);
    }

    @Override
    public void deleteUser(Long id) {
        this.customerRepository.deleteById(id);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return this.customerRepository.save(customer);
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public CustomResponseEntity<Customer> findById(Long id) {
        Optional<Customer> customer = this.customerRepository.findById(id);
        if (!customer.isPresent()) {
            return new CustomResponseEntity<>(1000, "Customer Not Found");
        }
        return new CustomResponseEntity<>(customer.get(), "Data Found");
    }

    @Override
    public CustomResponseEntity forgetUserName(ForgetUsernameDto forgetUsernameDto) {
        LOGGER.info("ForgetUsername Request Received...");

        Optional<Customer> optionalCustomer = customerRepository.findCustomerByCnicAndAccountNumber(
                forgetUsernameDto.getCnic(), forgetUsernameDto.getAccountNumber());

        if (!optionalCustomer.isPresent()) {
            throw new ServiceException("Customer Not Found");
        }

        Customer customer = optionalCustomer.get();

        ForgetUserAndPasswordResponse forgetUserAndPasswordResponse = new ForgetUserAndPasswordResponse();
        forgetUserAndPasswordResponse.setEmail(customer.getEmail());
        forgetUserAndPasswordResponse.setMobileNumber(customer.getMobileNumber());

        // Sending the email with username
        sendUserEmail(customer);

        LOGGER.info("Customer found with Account number [{}], sending username on email...", customer.getEmail());

        LOGGER.info("Email sent to [{}]", customer.getEmail());

        // Creating and returning the response
        CustomResponseEntity<ForgetUserAndPasswordResponse> response = new CustomResponseEntity<>("Username sent successfully");
        response.setData(forgetUserAndPasswordResponse);
        return response;

    }

    @Override
    public CustomResponseEntity forgetUserEmail(ForgetUsernameDto forgetUsernameDto) {
        LOGGER.info("ForgetUsername Request Received...");

        Optional<Customer> optionalCustomer = customerRepository.findCustomerByCnicAndAccountNumber(
                forgetUsernameDto.getCnic(), forgetUsernameDto.getAccountNumber());

        if (!optionalCustomer.isPresent()) {
            throw new ServiceException("Customer Not Found");
        }

        Customer customer = optionalCustomer.get();

        LOGGER.info("Customer details: {}", customer);

        ForgetUserAndPasswordResponse forgetUserAndPasswordResponse = new ForgetUserAndPasswordResponse();
        forgetUserAndPasswordResponse.setEmail(customer.getEmail());
        forgetUserAndPasswordResponse.setMobileNumber(customer.getMobileNumber());

        return new CustomResponseEntity<>(forgetUserAndPasswordResponse, "Customer Detail");
    }
//        Customer customer = findByEmailAddress(forgetUsernameDto.getAccountNumber());
//
//        if(customer != null)
//        {
//            LOGGER.info("Customer found with Email Address [{}], sending username on email...", forgetUsernameDto.getEmail());
//            //send username to user via email
//            sendUserEmail(customer);
//
//            // Kafka email send here
////            kafkaMessage = new KafkaMessageDto(forgetUsernameDto.getEmail(), "Forget Username", "Dear Customer, your username is " + customer.getUserName(), true, false);
////            sendMessage(kafkaMessage, "forgetUserName");
//
//            LOGGER.info("Email sent [{}]", forgetUsernameDto.getEmail());
//            response = new CustomResponseEntity<>("user name sent successfully");
//            return response;
//        }
//
//        LOGGER.info("Customer Email does not exists, verifying mobile number...");
//
//        customer = findByMobileNumber(forgetUsernameDto.getMobileNumber());
//
//        if(customer != null)
//        {
//            LOGGER.info("Customer found with Mobile Number [{}], sending username on SMS...", forgetUsernameDto.getMobileNumber());
//            // Kafka SMS send here
//            kafkaMessage = new KafkaMessageDto(forgetUsernameDto.getMobileNumber(), "UserName", "Dear Customer, your username is " + customer.getUserName(), false, true);
//            sendMessage(kafkaMessage, "forgetUserName");
//
//            LOGGER.info("SMS sent [{}]", forgetUsernameDto.getMobileNumber());
//            response = new CustomResponseEntity<>("user name sent successfully");
//            return response;
//        }
//
//        throw new ServiceException("Customer Not Found");
//    }

//    @Override
//    public CustomResponseEntity forgetPassword(ForgetUsernameDto forgetUsernameDto)
//    {
//        LOGGER.info("ForgetPassword request received");
//        try
//        {
//            // Lookup customer in database by e-mail
//            Customer customer = findByEmailAddress(forgetUsernameDto.getEmail());
//
//            if(customer == null)
//            {
//                LOGGER.error("Customer with Email [{}] does not exist", forgetUsernameDto.getEmail());
//                throw new ServiceException("Customer not found!!");
//            }
//
//            // Generate random 36-character string token for reset password
//            customer.setResetToken(UUID.randomUUID().toString());
//            AppConfiguration appConfiguration = this.appConfigurationImpl.findByName("RESET_EXPIRE_TIME"); // fetching token expire time in minutes
//
//            // Generate reset token expire time for reset password
//            customer.setResetTokenExpireTime(Long.parseLong(Util.dateFormat.format(DateUtils.addMinutes(new Date(), Integer.parseInt(appConfiguration.getValue())))));
//
//            // Save token to database
//            save(customer);
//
//            String resetAppUrl = "http://192.168.0.196:9094/v1/customer/verifyForgetPasswordToken?token=" + customer.getResetToken();
//
//            // Email message
//            kafkaMessage = new KafkaMessageDto(forgetUsernameDto.getEmail(), "Forget Password", "Dear Customer, To reset your password, click the link below:\n" + resetAppUrl, true, false);
//            sendMessage(kafkaMessage, "forgetUserName");
//
//            LOGGER.info("Email sent [{}]", forgetUsernameDto.getEmail());
//            response = new CustomResponseEntity<>("user name sent successfully");
//            return response;
//        }
//        catch (Exception e)
//        {
//            LOGGER.error("Password reset link send failed...");
//            LOGGER.error(e.getMessage());
//            throw new ServiceException("Oops! Password reset link send failed...");
//        }
//    }

    /*
    @Override
    public CustomResponseEntity verifyResetPasswordToken(String token)
    {
        LOGGER.info("VerifyResetPasswordToken Request Received...");
        LOGGER.info("Verifying ResetToken [{}]", token);

        try
        {
            Customer customer = findByResetToken(token);

            if(customer != null) // Token found in DB
            {
                if(customer.getResetTokenExpireTime() > Long.parseLong(Util.dateFormat.format(new Date())))
                {
                    LOGGER.info("Customer ResetPassword token found and valid for customer [{}]...", customer.getMobileNumber());
                    response = new CustomResponseEntity<>(token,"Token found and valid");
                    return response;
                }
                else
                {
                    LOGGER.error("Reset Token [{}] has been expired for customer [{}], replying...", customer.getResetToken(), customer.getMobileNumber());
                    throw new ServiceException("Oops! This is an invalid password reset link.");
                }
            }

            throw new ServiceException("Oops! This is an invalid password reset link.");
        }
        catch (Exception e)
        {
            LOGGER.error("Password reset failed...");
            LOGGER.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }*/

    /*@Override
    public CustomResponseEntity resetPassword(ResetPasswordDto resetPasswordDto)
    {
        LOGGER.info("ResetPassword Request Received...");

        try
        {
            Customer resetCustomerPassword = findByResetToken(resetPasswordDto.getToken()); // Find the user associated with the reset token

            if(resetCustomerPassword != null) // This should always be non-null, but we check just in case
            {
                resetCustomerPassword.setPassword(resetPasswordDto.getPassword()); // Set new password
                resetCustomerPassword.setResetToken(null); // Set the reset token to null, so it cannot be used again
                save(resetCustomerPassword); // Save customer

                LOGGER.info("Password reset successful for customer [{}]", resetCustomerPassword.getMobileNumber());
                response = new CustomResponseEntity<>("You have successfully reset your password. You may now login.");
                return response;
            }

            LOGGER.error("Password reset failed...");
            throw new ServiceException("Oops! This is an invalid password reset link.");
        }
        catch (Exception e)
        {
            LOGGER.error("Password reset failed...");
            LOGGER.error(e.getMessage());
            throw new ServiceException("Oops! Password reset failed..");
        }
    }
*/

    @Override
    public CustomResponseEntity forgetPassword(ForgetPasswordRequestDto forgetPasswordRequestDto) {
        Optional<Customer> optionalCustomer = customerRepository.findCustomerByCnicAndAccountNumber(forgetPasswordRequestDto.getCnic(), forgetPasswordRequestDto.getAccountNumber());

        if (!optionalCustomer.isPresent()) {
            throw new ServiceException("Customer Not Found");
        }

        Customer customer = optionalCustomer.get();

        // Check if the new password is null
        if (forgetPasswordRequestDto.getPassword() == null || forgetPasswordRequestDto.getPassword().isEmpty()) {
            return CustomResponseEntity.error("Password cannot be null or empty.");
        }

        if (forgetPasswordRequestDto.getPassword().equals(customer.getPassword())) {
            return CustomResponseEntity.error("Password cannot be the same as the old password.");
        }

        customer.setPassword(forgetPasswordRequestDto.getPassword()); // Hash the new password

        customerRepository.save(customer);

        // Created response object
        ForgetUserAndPasswordResponse forgetUserAndPasswordResponse = new ForgetUserAndPasswordResponse();
        forgetUserAndPasswordResponse.setEmail(customer.getEmail());
        forgetUserAndPasswordResponse.setMobileNumber(customer.getMobileNumber());

        emailService.sendPasswordResetNotification(customer.getEmail());

        // Return a successful response
        return new CustomResponseEntity<>(forgetUserAndPasswordResponse, "Password reset successfully");
    }

    @Override
    public CustomResponseEntity verifyResetPasswordToken(String token) {
        Customer customer = customerRepository.findByResetToken(token);
        if (customer == null || customer.getResetTokenExpireTime() < System.currentTimeMillis()) {
            return CustomResponseEntity.error("Invalid or expired reset token.");
        }
        return new CustomResponseEntity<>("Token verified.");
    }

    @Override
    public CustomResponseEntity resetPassword(ResetPasswordDto resetPasswordDto, HttpSession session) {
        /*Customer customer = customerRepository.findByResetToken(resetPasswordDto.getToken());
        if (customer == null || customer.getResetTokenExpireTime() < System.currentTimeMillis()) {
            return CustomResponseEntity.error("Invalid or expired reset token.");
        }*/

        // Retrieve the token from the session
        String token = (String) session.getAttribute("resetToken");
        System.out.println("Retrieved token from session: " + token);

        if (token == null) {
            return CustomResponseEntity.error("No reset token found in session.");
        }

   /*     Customer customer = customerRepository.findByUserName(resetPasswordDto.getUsername());
        if (customer == null ) {
            return CustomResponseEntity.error("Invalid or username mismatch.");
        }*/

        Customer customer = customerRepository.findByResetToken(token);
        if (!customer.getResetToken().equals(token) || customer.getResetTokenExpireTime() < System.currentTimeMillis()) {
            return CustomResponseEntity.error("Invalid or expired reset token.");
        }

        if(customer == null || !customer.getUserName().equals(resetPasswordDto.getUsername())){
            return CustomResponseEntity.error("Invalid or miss match user");
        }

        if (resetPasswordDto.getPassword().equals(customer.getPassword())) {
            return CustomResponseEntity.error("Password cannot be the same as the old password.");
        }

        customer.setPassword(resetPasswordDto.getPassword()); // Hash the password before saving it
        customer.setResetToken(null);
        customer.setResetTokenExpireTime(null);
        customerRepository.save(customer);

        // Invalidate the session after successful password reset
        session.invalidate();
        System.out.println("Session invalidated after password reset.");

        emailService.sendPasswordResetNotification(customer.getEmail());

        return new CustomResponseEntity<>("Password reset successfully.");
    }


    @Override
    public Customer findByResetToken(String resetToken)
    {
        return this.customerRepository.findByResetToken(resetToken);
    }

    @Override
    public Customer findByEmailAddress(String email)
    {
        return this.customerRepository.findByEmail(email);
    }

    @Override
    public Customer findByMobileNumber(String mobileNumber) {
        return this.customerRepository.findByMobileNumber(mobileNumber);
    }

    @Override
    public Customer findByUserName(String userName) {
        return this.customerRepository.findByUserName(userName);
    }

    public List<Customer> findAllUsers()
    {
        return this.customerRepository.findAll();
    }

    public Customer updateCustomer(CustomerDto customerDto)
    {

        Customer updateCustomer = this.customerRepository.findByEmail(customerDto.getEmail());

        if(updateCustomer == null)
        {
            throw new ServiceException(String.format("User with email %s not found", customerDto.getEmail()));
        }

        updateCustomer.setFirstName(customerDto.getFirstName());
        updateCustomer.setLastName(customerDto.getLastName());
        updateCustomer.setEmail(customerDto.getEmail());

        save(updateCustomer);
        LOGGER.info("Customer has been updated with Id {}", updateCustomer.getId());

        return ResponseEntity.ok(updateCustomer).getBody();
    }

//    private Boolean accountExist(String cnic , String mobile , String accountNumber)
//    {
//        Client client = Client.create();
//        client.setConnectTimeout(5 * 1000);
//        client.setReadTimeout(5 * 1000);
//        WebResource webResource = null;
//
//        try
//        {
//            // Create a trust manager that does not validate certificate chains
//            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            }
//            };
//
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//            // Create all-trusting host name verifier
//            HostnameVerifier allHostsValid = (hostname, session) -> true;
//
//            // Install the all-trusting host verifier
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//
//            String url = String.format(URL, cnic , mobile , accountNumber);
//            LOGGER.info("Calling URL [" + url + "]");
//            webResource = client.resource(url);
//
//            ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
//                    .accept(MediaType.APPLICATION_JSON)
//                    .get(ClientResponse.class);
//
//            if(clientResponse != null)
//            {
//                if(clientResponse.getStatus() != 302)
//                {
//                    LOGGER.error("Account Service down, rejecting!!");
//                    return false;
//                }
//                AccountDto accountList = clientResponse.getEntity(new GenericType<AccountDto>() {});
//
//                if(accountList != null && !accountList.getAccountList().isEmpty() )
//                {
//                    LOGGER.info("Account Service response found For Customer [{}], proceeding...", cnic);
//                    return true;
//                }
//            }
//            else
//            {
//                LOGGER.error("Failed to Get response from Account Service For Customer [{}], rejecting...", cnic);
//            }
//            return false;
//        }
//        catch (Exception e)
//        {
//            LOGGER.error("Failed to Get response from Account Service For Customer [{}], rejecting...", cnic);
//            LOGGER.error(e.getMessage());
//            return false;
//        }
//    }

//    public CustomerDto accountExist(String cnic, String mobile, String accountNumber) {
//        try {
//            // Create a trust manager that does not validate certificate chains
//            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            }};
//
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//            // Create all-trusting host name verifier
//            HostnameVerifier allHostsValid = (hostname, session) -> true;
//
//            // Install the all-trusting host verifier
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//
//            // Build URL with path variables
//            String url = URL;
//            URI uri = UriComponentsBuilder.fromUriString(url)
//                    .buildAndExpand(cnic, mobile, accountNumber)
//                    .toUri();
//
//            // Set headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            // Create HttpEntity with headers
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            // Make HTTP GET request
//            ResponseEntity<CustomerDto> response = restTemplate.exchange(
//                    uri,
//                    HttpMethod.GET,
//                    entity,
//                    CustomerDto.class
//            );
//
//            // Handle response
//            if (response.getStatusCode() == HttpStatus.FOUND) { // 302 status code
//                CustomerDto customerDto = response.getBody();
//                if (customerDto != null) {
//                    // Print or log customerDto to verify its content
//                    System.out.println("Received CustomerDto: " + customerDto.toString());
//
//                    // Check if accountList is populated
//                    if (customerDto.getAccountList() != null && !customerDto.getAccountList().isEmpty()) {
//                        // Customer found with accountList
//                        return customerDto;
//                    } else {
//                        // Customer found but accountList is empty or null
//                        // Handle accordingly
//                        return customerDto;
//                    }
//                } else {
//                    // No customer found
//                    return null;
//                }
//            } else {
//                // Handle error response or non-302 status
//                return null;
//            }
//
//        } catch (Exception e) {
//            // Handle exceptions
//            e.printStackTrace(); // Replace with proper logging
//            return null;
//        }
//    }


//    public CustomerDto accountExist(String cnic, String mobile, String accountNumber) {
//        try {
//            // Create a trust manager that does not validate certificate chains
//            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            }};
//
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//            // Create all-trusting host name verifier
//            HostnameVerifier allHostsValid = (hostname, session) -> true;
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//
//            // Build URL with path variables
//            URI uri = UriComponentsBuilder.fromHttpUrl(URL)
//                    .queryParam("cnic", cnic)
//                    .queryParam("mobileNumber", mobile)
//                    .queryParam("accountNumber", accountNumber)
//                    .build()
//                    .toUri();
//
//            // Log the full request URL
//            LOGGER.info("Request URL: " + uri.toString());
//
//            // Set headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            // Create HttpEntity with headers
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            // Make HTTP GET request
//            ResponseEntity<CustomerDto> response = restTemplate.exchange(
//                    uri,
//                    HttpMethod.GET,
//                    entity,
//                    CustomerDto.class
//            );
//
//            // Handle response
//            if (response.getStatusCode() == HttpStatus.OK) { // 200 status code
//                CustomerDto customerDto = response.getBody();
//                if (customerDto != null) {
//                    // Print or log customerDto to verify its content
//                    LOGGER.info("Received CustomerDto: " + customerDto.toString());
//
//                    // Check if accountList is populated
//                    if (customerDto.getAccountNumber() != null && !customerDto.getAccountNumber().isEmpty()) {
//                        // Customer found with accountList
//                        return customerDto;
//                    } else {
//                        // Customer found but accountList is empty or null
//                        // Handle accordingly
//                        return customerDto;
//                    }
//                } else {
//                    // No customer found
//                    return null;
//                }
//            } else {
//                // Handle error response or non-200 status
//                LOGGER.error("Unexpected response status: " + response.getStatusCode());
//                return null;
//            }
//
//        } catch (Exception e) {
//            // Handle exceptions
//            LOGGER.error("Exception occurred: ", e);
//            return null;
//        }
//    }
//

    public SignUpResponse accountExist(String cnic, String mobile, String accountNumber) {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(URL)
                    .queryParam("cnic", cnic)
                    .queryParam("mobileNumber", mobile)
                    .queryParam("accountNumber", accountNumber)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri.toString());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make HTTP GET request
            ResponseEntity<SignUpResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    SignUpResponse.class
            );

            // Handle response
            if (response.getStatusCode() == HttpStatus.OK) { // 200 status code
                SignUpResponse responseDto = response.getBody();
                if (responseDto != null) {
                    // Print or log responseDto to verify its content
                    LOGGER.info("Received CustomerDto: " + responseDto.toString());

                    // Create new CustomerDto and map fields
                    SignUpResponse signUpResponse = new SignUpResponse();
                    signUpResponse.setCustomer(responseDto.getCustomer());
                    signUpResponse.setAccount(responseDto.getAccount());
                    signUpResponse.setCbsBankDto(responseDto.getCbsBankDto());
                    signUpResponse.setGlobalId(responseDto.getGlobalId());
                    Customer customer = new Customer();
                    customer.setFirstName(responseDto.getCustomer().getFirstName());
                    customer.setLastName(responseDto.getCustomer().getLastName());
                    signUpResponse.setCustomer(customer);
                    signUpResponse.setLastName(responseDto.getCustomer().getLastName());
                    signUpResponse.setEmail(responseDto.getCustomer().getEmail());
                    signUpResponse.setCnicNumber(responseDto.getGlobalId().getCnicNumber());

                    // Return the populated CustomerDto
                    return signUpResponse;
                } else {
                    // No customer found
                    return null;
                }
            } else {
                // Handle error response or non-200 status
                LOGGER.error("Unexpected response status: " + response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            // Handle exceptions
            LOGGER.error("Exception occurred: ", e);
            return null;
        }
    }

    public CustomResponseEntity dashboard(Long customerId, Long accountId) {

        LOGGER.info("Dashboard Request Received...");

        try {
            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(dashBoardCBSURL)
                    .queryParam("customerId", customerId)
                    .queryParam("accountId", accountId)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + dashBoardCBSURL);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make HTTP GET request
            ResponseEntity<DashBoardResponseDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    DashBoardResponseDto.class
            );

            // Handle response
            if (response.getStatusCode() == HttpStatus.OK) {
                DashBoardResponseDto dashBoardResponseDto = response.getBody();

                LOGGER.info(dashBoardResponseDto.toString());
                Map<String, Object> data = new HashMap<>();
                data.put("dasboard", dashBoardResponseDto);
                return this.response = new CustomResponseEntity<>(data, "SUCCESS");

            } else {
                // Handle error response or non-200 status
                LOGGER.error("Unexpected response status: " + response.getStatusCode());
                return CustomResponseEntity.error("CBS is Down");
            }
        } catch (RestClientException e) {
            LOGGER.error("Exception occurred: ", e);
            return CustomResponseEntity.error("CBS is Down");
        }

    }

    public CustomResponseEntity setDefaultAccount(String accountNumber) {

        LOGGER.info("SetDefaultAccount Request Received...");

        try {
            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(setdefaultaccountCBSURL)
                    .queryParam("accountNumber", accountNumber)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + setdefaultaccountCBSURL);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make HTTP GET request
            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Handle response
            if (response.getStatusCode() == HttpStatus.OK) {
                String setDefaultAccountResponse = response.getBody();

                LOGGER.info(setDefaultAccountResponse);
                Map<String, Object> data = new HashMap<>();
                data.put("DefaultAccountStatus", setDefaultAccountResponse);
                assert setDefaultAccountResponse != null;
                if(setDefaultAccountResponse.equals("Invalid Account Number"))
                {
                    return this.response = new CustomResponseEntity<>(100, "False", data);
                }
                return this.response = new CustomResponseEntity<>(data, "SUCCESS");

            } else {
                // Handle error response or non-200 status
                LOGGER.error("Unexpected response status: " + response.getStatusCode());
                return CustomResponseEntity.error("CBS is Down");
            }
        } catch (RestClientException e) {
            LOGGER.error("Exception occurred: ", e);
            return CustomResponseEntity.error("CBS is Down");
        }

    }
    public void sendMessage(Object message, String topicName) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.info("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                LOGGER.error("Unable to send message=[" +
                        message + "] due to : " + ex.getMessage());
            }
        });
    }

    public boolean setCustomerStatus(String email, String mobileNumber) {
        Customer customer = findByEmailAddress(email);
        if (customer != null) {
            customer.setStatus(CustomerStatus.ACTIVE.getCode());
            save(customer);
            LOGGER.info("Customer with [{}] Status has been updated successfully", email);
            return true;
        }

        LOGGER.error("No Customer found to update the status");
        return false;
    }

    @Override
    public boolean findBySessionToken(String sessionToken, long currentTime) {
        return this.customerRepository.isValidToken(sessionToken, currentTime);
    }

    public boolean sendUserEmail(Customer customer) {
        String emailOtp = Util.generateOTP(5); //when deliveryPreference is set to both
        String smsOtp = Util.generateOTP(5);

        OTP otp = new OTP();
        otp.setMobileNumber(customer.getMobileNumber());
        otp.setEmail(customer.getEmail());
        otp.setIsExpired(false);
        otp.setIsVerified(false);
        otp.setCreateDateTime(Long.parseLong(Util.dateFormat.format(new Date())));
        otp.setReason("Forget Username");
        //AppConfiguration appConfiguration = this.appConfigurationImpl.findByName("OTP_EXPIRE_TIME");
        otp.setExpiryDateTime(System.currentTimeMillis() + 60000);

        // Sending email with OTP
        try {
            //if(deliveryPreference.equals(DeliveryPreference.EMAIL.getValue())){
            otp.setSmsMessage("Dear Customer, your username is " + customer.getUserName());
            otp.setEmailOtp(emailOtp);
            sendForgetUserEmail(customer.getEmail(), customer.getUserName());
            LOGGER.info("Email sent successfully to [{}]", customer.getEmail());

            // Save OTP log
            if (save(customer).getId() != null) {
                LOGGER.info("OTP has been saved with Id: {}", otp.getId());
                return true;
            }
            // }
//            if(deliveryPreference==DeliveryPreference.SMS.getValue()){
//                otp.setSmsOtp(smsOtp);
//                otp.setSmsMessage("Dear Customer, your OTP to complete your request is " + smsOtp);
//                //  sendOtpSms(OTPDto.getMobileNumber(), OTPDto.getReason(), smsOtp);
//                LOGGER.info("Sms sent successfully to [{}]", emailOTPSendDto.getMobileNumber());
//
//                // Save OTP log
//                if (save(otp).getId() != null) {
//                    LOGGER.info("OTP has been saved with Id: {}", otp.getId());
//                    return true;
//                }
//            }
//
//            if(deliveryPreference==DeliveryPreference.BOTH.getValue()){
//                otp.setSmsOtp(smsOtp);
//                otp.setEmailOtp(emailOtp);
//                otp.setSmsMessage("Dear Customer, your OTPs to complete your request are : email " + emailOtp+" sms "+smsOtp);
//                sendOtpEmail(emailOTPSendDto.getEmail(), "Usename of user", emailOtp);
//                //  sendOtpSms(OTPDto.getMobileNumber(), OTPDto.getReason(), smsOtp);
//                LOGGER.info("Email sent successfully to [{}]", emailOTPSendDto.getEmail());
//                LOGGER.info("Sms sent successfully to [{}]", emailOTPSendDto.getMobileNumber());
//
//                // Save OTP log
//                if (save(otp).getId() != null) {
//                    LOGGER.info("OTP has been saved with Id: {}", otp.getId());
//                    return true;
//                }
//            }

        } catch (Exception e) {
            LOGGER.error("Failed to send email to [{}]: {}", customer.getEmail(), e.getMessage());
            LOGGER.info("Failed to send sms to [{}]", customer.getMobileNumber());
        }
        return false;
    }

    private void sendForgetUserEmail(String email, String username) {
        EmailDto dto = new EmailDto();
        dto.setRecipient(email);
        dto.setSubject("DigiBank");
        dto.setBody("We received a request to retrieve the username associated with your account. Please find your username below:\n" +
                "\n" +
                "Username: " + username);
        emailService.sendSimpleMessage(dto);
    }



    @Override
    public SuggestedUserName generateUniqueUsernames(String userName) {
        Boolean isValid = customerRepository.existsByUserName(userName);
        if (!isValid) {
            return new SuggestedUserName(null, true);
        }
        List<String> uniqueUsernames = new ArrayList<>();
        String baseUsername = (userName).toLowerCase().replaceAll("\\s+", ""); // Generate base username

        int count = 0;
        int suffix = 1;
        while (count < 3) {
            String username = baseUsername + suffix;
            if (!customerRepository.existsByUserName(username)) {
                uniqueUsernames.add(username);
                count++;
            }
            suffix++;
        }

        return new SuggestedUserName(uniqueUsernames, false);
    }

    @Override
    public Boolean validateUser(String mobileNumber, String email) {
        Optional<Customer> user = customerRepository.findByMobileNumberAndEmail(mobileNumber, email);
        return user.isPresent();
    }
}