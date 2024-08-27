package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.Util.Util;
import com.iconsult.userservice.domain.OTP;
import com.iconsult.userservice.dto.DefaultAccountDto;
import com.iconsult.userservice.dto.EmailDto;
import com.iconsult.userservice.enums.AccountStatusCode;
import com.iconsult.userservice.enums.CustomerStatus;
import com.iconsult.userservice.enums.ResponseCodes;
import com.iconsult.userservice.exception.ServiceException;
import com.iconsult.userservice.model.dto.request.*;
import com.iconsult.userservice.model.dto.response.DashBoardResponseDto;
import com.iconsult.userservice.model.dto.response.ForgetUserAndPasswordResponse;
import com.iconsult.userservice.model.dto.response.KafkaMessageDto;
import com.iconsult.userservice.model.dto.response.SignUpResponse;
import com.iconsult.userservice.model.entity.*;
import com.iconsult.userservice.model.mapper.CustomerMapper;
import com.iconsult.userservice.repository.AccountCDDetailsRepository;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CustomerServiceImpl implements CustomerService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final String URL = "http://localhost:8081/customer/get/cnic/mobileNumber/accountNumber";
    private final String dashBoardCBSURL = "http://localhost:8081/account/dashboard";
    private final String setdefaultaccountCBSURL = "http://localhost:8081/customer/setdefaultaccount";

    private final String accountURL = "http://localhost:8081/account/getAccount";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    private KafkaMessageDto kafkaMessage;

    private CustomResponseEntity response;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    GenericDao<Customer> customerGenericDao;

    @Autowired
    GenericDao<Account> accountGenericDao;
    @Autowired
    GenericDao<AccountCDDetails> accountCDDetailsGenericDao;

    @Autowired
    private AccountCDDetailsRepository accountCDDetailsRepository;
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

    private AccountStatusCode accountStatusCode;

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

        Account accountByAccountNumber = accountRepository.getAccountByAccountNumber(customerDto.getAccount().getAccountNumber());

        if (accountByAccountNumber != null) {
            LOGGER.error("Customer already exists with userName [" + customerDto.getAccount().getAccountNumber() + "], cannot allow signup, rejecting...");
            throw new ServiceException(String.format("Customer with accountNumber %s already exists", customerDto.getAccount().getAccountNumber()));
        }

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
        if (signUpDto == null) {
            throw new ServiceException("SignUp data cannot be null");
        }

        // Check for existing data
        checkExistingData(signUpDto);

        // Validate the security picture
        validateImage(signUpDto.getSecurityPictureId());

        // Validate account information
        AccountDto accountDto = signUpDto.getAccountDto();
        if (accountDto == null || accountDto.getAccountNumber() == null || accountDto.getAccountNumber().isBlank()) {
            throw new ServiceException("Account number should not be null or empty");
        }

        // Build URL and make the HTTP request
        ResponseEntity<AccountDto> response = makeAccountRequest(accountDto);

        // Check if account exists in response
        if (response.getBody() == null) {
            return CustomResponseEntity.error("Account does not exist in CBS");
        }

        // Check if account is already registered
        if (accountRepository.findByAccountNumber(accountDto.getAccountNumber()) != null) {
            return CustomResponseEntity.error("Account already registered: " + accountDto.getAccountNumber());
        }

        // Create and save customer with account
        Customer customer = createCustomer(signUpDto, response.getBody());
        customerRepository.save(customer);
        Account account = accountRepository.findByAccountNumberAndCustomerCnic(accountDto.getAccountNumber(), accountDto.getCustomer().getCnic());
        AccountCDDetails accountCDDetails = new AccountCDDetails(account, account.getAccountBalance(),0.0,accountDto.getLastCredit(),accountDto.getLastDebit());
        accountCDDetailsRepository.save(accountCDDetails);// This will cascade and save the account

        // Return success response
        return new CustomResponseEntity<>("Customer registered successfully");
    }

    private void checkExistingData(SignUpDto signUpDto) {
        if (customerRepository.existsByCnic(signUpDto.getCnic())) {
            throw new ServiceException("An account with this CNIC already exists");
        }
        if (customerRepository.existsByEmail(signUpDto.getEmail())) {
            throw new ServiceException("An account with this email already exists");
        }
        if (customerRepository.existsByUserName(signUpDto.getUserName())) {
            throw new ServiceException("An account with this username already exists");
        }
        if (customerRepository.existsByMobileNumber(signUpDto.getMobileNumber())) {
            throw new ServiceException("An account with this mobile number already exists");
        }
    }

    private void validateImage(Long securityPictureId) {
        if (!imageVerificationRepository.existsById(securityPictureId)) {
            throw new ServiceException("Image does not exist");
        }
    }


    private ResponseEntity<AccountDto> makeAccountRequest(AccountDto accountDto) {
        URI uri = UriComponentsBuilder.fromHttpUrl(accountURL)
                .queryParam("accountNumber", accountDto.getAccountNumber())
                .queryParam("bankName", "HBL")
                .build()
                .toUri();

        LOGGER.info("Request URL: " + uri.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, entity, AccountDto.class);
    }

    private Customer createCustomer(SignUpDto signUpDto, AccountDto responseDto) {
        Customer customer = new Customer();
        customer.setMobileNumber(signUpDto.getMobileNumber());
        customer.setFirstName(signUpDto.getFirstName());
        customer.setLastName(signUpDto.getLastName());
        customer.setCnic(signUpDto.getCnic());
        customer.setEmail(signUpDto.getEmail());
        customer.setUserName(signUpDto.getUserName());
        customer.setPassword(signUpDto.getPassword());
        customer.setSecurityPicture(imageVerificationRepository.findById(signUpDto.getSecurityPictureId())
                .orElseThrow(() -> new ServiceException("Image does not exist")).getName());
        customer.setStatus(signUpDto.getStatus());
        customer.setResetToken(signUpDto.getResetToken());
        customer.setResetTokenExpireTime(signUpDto.getResetTokenExpireTime());
        if (customer.getAccountList() == null) {
            customer.setAccountList(new ArrayList<>());
        }

        // Create and add account
        Account account = new Account();
        account.setAccountNumber(signUpDto.getAccountDto().getAccountNumber());
        account.setAccountBalance(responseDto.getAccountBalance());
        account.setAccountType(signUpDto.getAccountDto().getAccountType());
        account.setAccountDescription(signUpDto.getAccountDto().getAccountDescription());
        account.setAccountStatus(AccountStatusCode.ACTIVE.getCode());
        account.setIbanCode(responseDto.getIbanCode());
        account.setAccountOpenDate(new Date());
        account.setProofOfIncome(signUpDto.getAccountDto().getProofOfIncome());
        account.setCustomer(customer);
        account.setDefaultAccount(true);

        customer.getAccountList().add(account);

        return customer;
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
    public CustomResponseEntity fetchUserData(Long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        CustomerDto customerDto = customerMapper.jpeToDto(customer);
        DefaultAccountDto defaultAccountDto = new DefaultAccountDto();
        if(Objects.isNull(customer)){
            LOGGER.info("Error Receiving User Details With Id  : " + id);
            return CustomResponseEntity.error("Error Receiving User Details With Id  : \" + id");
        }
        List<Account> accounts = customer.getAccountList();

        // Stream and filter to get the default account
        Optional<Account> defaultAccount = accounts.stream()
                .filter(Account::getDefaultAccount)
                .findFirst();
        if (!defaultAccount.isEmpty()){
            defaultAccountDto.setDefaultAccountBalance(String.valueOf(defaultAccount.get().getAccountBalance()));
            defaultAccountDto.setAccountNumber(defaultAccount.get().getAccountNumber());
            defaultAccountDto.setFirstName(customer.getFirstName());
            defaultAccountDto.setLastName(customer.getLastName());
            defaultAccountDto.setAccountType(defaultAccount.get().getAccountType());
        }
        return new CustomResponseEntity<>(defaultAccountDto , "Default Account Retrieved Successfully");
    }

    @Override
    public Boolean validateUser(String mobileNumber, String email) {
        Optional<Customer> user = customerRepository.findByMobileNumberAndEmail(mobileNumber, email);
        return user.isPresent();
    }

    public CustomResponseEntity dashboard(Long customerId) {
        String jpql = "Select c from Customer c where c.id = :customerId";
        Map<String, Object> param = new HashMap<>();
        param.put("customerId",customerId);
        Customer customer = customerGenericDao.findOneWithQuery(jpql, param);
        if(customer == null){
            return CustomResponseEntity.error("Customer not found ");
        }
        List<Account> accountList = customer.getAccountList();
        if(accountList.isEmpty()){
            return CustomResponseEntity.error("The customer does not have any accounts");
        }

        Optional<Account> defaultAccount = accountList.stream()
                .filter(Account::getDefaultAccount)
                .findFirst();

        DashBoardResponseDto dashBoardResponseDto = new DashBoardResponseDto();
        if(defaultAccount.isPresent()){
            AccountCDDetails accountCDDetails = defaultAccount.get().getAccountCdDetails();
            dashBoardResponseDto.setLastDebit(accountCDDetails.getDebit());
            dashBoardResponseDto.setLastCredit(accountCDDetails.getCredit());
            dashBoardResponseDto.setTotalBalance(totalBalance(accountList));
            dashBoardResponseDto.setAccountList(accountList);
            return new CustomResponseEntity(dashBoardResponseDto,"Success");
        }
        defaultAccount = Optional.ofNullable(accountList.get(0));
        AccountCDDetails accountCDDetails = defaultAccount.get().getAccountCdDetails();
        dashBoardResponseDto.setLastDebit(accountCDDetails.getDebit() == null ? 0 : accountCDDetails.getDebit());
        dashBoardResponseDto.setLastCredit(accountCDDetails.getCredit() == null ? 0 : accountCDDetails.getCredit());
        dashBoardResponseDto.setTotalBalance(totalBalance(accountList));
        dashBoardResponseDto.setAccountList(accountList);
        LOGGER.info("Dashboard Request Received...");
        return new CustomResponseEntity(dashBoardResponseDto,"Success");
    }
    private Double totalBalance(List<Account> accountList){
        Double availableBalance = 0.0;
        for(Account account : accountList){
            availableBalance += account.getAccountBalance();
        }
        return availableBalance;
    }
    public CustomResponseEntity setDefaultAccount(String accountNumber, Boolean setDefaultAccount) {

        LOGGER.info("SetDefaultAccount Request Received...");
        Map<String, Object> param = new HashMap<>();
        param.put("accountNumber",accountNumber);
        String jpql = "Select a from Account a where a.accountNumber = :accountNumber";

        Optional<Account> account = Optional.ofNullable(accountGenericDao.findOneWithQuery(jpql, param));
        if(account.isPresent()){
            List<Account> accountList = account.get().getCustomer().getAccountList();
            Account checkAccount = accountList.stream()
                    .filter(defaultAccount -> defaultAccount.getDefaultAccount() == true)
                    .findFirst()
                    .orElse(null);
            if(setDefaultAccount){
                account.get().setDefaultAccount(true);
                LOGGER.info("Account found : ", account.get().getAccountNumber());
                accountRepository.save(account.get());
                return new CustomResponseEntity(account, "Success");

            } else if (setDefaultAccount == false) {
                account.get().setDefaultAccount(false);
                accountRepository.save(account.get());
                return new CustomResponseEntity(account, "Success");
            } else return CustomResponseEntity.error("Invalid Status ");

        }
        LOGGER.info("Account not found");
        return CustomResponseEntity.error("Account not found");
    }

}