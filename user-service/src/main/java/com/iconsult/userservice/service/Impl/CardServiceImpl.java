package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;

import com.iconsult.userservice.dto.UserActivityRequest;
import com.iconsult.userservice.custome.Regex;
import com.iconsult.userservice.model.dto.request.*;
import com.iconsult.userservice.model.dto.response.CardApprovalResDto;
import com.iconsult.userservice.model.entity.*;
import com.iconsult.userservice.model.mapper.CardMapper;
import com.iconsult.userservice.model.mapper.CardRequestMapper;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.CardRepository;
import com.iconsult.userservice.repository.CardRequestRepository;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.service.CardService;
import com.iconsult.userservice.service.UserActivityService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
//import com.zanbeel.otp_service.config.CustomApiResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.*;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CardServiceImpl implements CardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    private final String URL = "http://localhost:8081/cards/verifyCard";
    private final String addCardURL = "http://localhost:8081/cards/addCard";

    private final String otpUrl = "http://localhost:9000/v1/otp";


    @Autowired
    private GenericDao<Customer> customerGenericDao;

    @Autowired
    private UserActivityService userActivityService;
    @Autowired
    private GenericDao<Account> accountGenericDao;

    @Autowired
    private GenericDao<Card> cardGenericDao;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private CardRequestMapper cardRequestMapper;
    @Autowired
    private CardRequestRepository requestRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    CustomResponseEntity customResponseEntity;

    private final String URLL = "http://localhost:8081/cards/setPin";
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    Regex regex;

    public CustomResponseEntity cardExist(CardDto cardDto) {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(URL)
                    .queryParam("cardNumber", cardDto.getCardNumber())
                    .queryParam("cardHolderName", cardDto.getCardHolderName())
                    .queryParam("cardExpiry", cardDto.getExpiryDate())
                    .queryParam("cardCVV", cardDto.getCvv())
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
            ResponseEntity<CustomResponseEntity> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CustomResponseEntity.class
            );

            // Handle response
            if (response.getStatusCode() == HttpStatus.OK) { // 200 status code
                CustomResponseEntity<Card> responseDto = response.getBody();
                if (responseDto != null) {
                    // Print or log responseDto to verify its content
                    LOGGER.info("Received CustomerDto: " + responseDto.getMessage());
                    if (!responseDto.isSuccess()) return responseDto;

                    String jpql = "SELECT c FROM Customer c WHERE c.id = :id";
                    Map<String, Object> params = new HashMap<>();
                    params.put("id", cardDto.getCid());

                    Customer customer = customerGenericDao.findOneWithQuery(jpql, params);

                    if (customer == null) return CustomResponseEntity.error("Customer Does not exist");
                    // Finding the card by cardNumber
                    Account account = customer.getAccountList().stream()
                            .filter(c -> cardDto.getAccountNumber().equals(c.getAccountNumber()))
                            .findFirst()  // Gets the first match or an empty Optional
                            .orElse(null); // Returns null if no match is found


                    Card duplicateCard = account.getCardList().stream()
                            .filter(c -> cardDto.getCardNumber().equals(c.getCardNumber()))
                            .findFirst()  // Gets the first match or an empty Optional
                            .orElse(null); // Returns null if no match is found

                    if (duplicateCard != null) {
                        LOGGER.error("Card Already Exists");
                        return CustomResponseEntity.error("Card Already Exists");
                    }


                    Map<String, Object> dataMap = (Map<String, Object>) responseDto.getData();
                    Card card = new Card();
                    card.setAccount(account);
                    card.setCardNumber((String) dataMap.get("cardNumber"));
                    card.setCardHolderName((String) dataMap.get("cardHolderName"));
                    card.setCardType((String) dataMap.get("cardType"));
                    card.setCvv((String) dataMap.get("cvv"));
                    card.setActive((Boolean) dataMap.get("active"));
                    card.setExpiryDate((String) dataMap.get("expiryDate"));
                    card.setIsCreditCard((Boolean) dataMap.get("creditCard"));

                    card = cardGenericDao.saveOrUpdate(card);

                    if (card.getCardId() != null) ;
                    {
                        LOGGER.info("Card has been saved Successfully with ID {}", card.getCardId());
                        return responseDto;
                    }

                } else {
                    // No customer found
                    return CustomResponseEntity.error("Unable to Process!");
                }
            } else {
                // Handle error response or non-200 status
                LOGGER.error("Unexpected response status: " + response.getStatusCode());
                return CustomResponseEntity.error("Unable to Process!");
            }

        } catch (Exception e) {
            // Handle exceptions
            LOGGER.error("Exception occurred: ", e);
            return CustomResponseEntity.error("Unable to Process!");
        }
    }

    @Override
    public CustomResponseEntity getAllCardById(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) return CustomResponseEntity.error("No Cards Exist");

        List<Card> listOfCard = account.getCardList();

        return new CustomResponseEntity<>(listOfCard, "Card List");
    }

    @Override
    public CustomResponseEntity updateCardStatus(Long cardNumber, String accountNumber, Boolean status) {

        if (cardNumber == null) {
            LOGGER.error("Card Number is Null");
            return CustomResponseEntity.error("Card Number is empty");
        } else if (accountNumber == null) {
            LOGGER.error("Customer ID is Null");
            return CustomResponseEntity.error("Customer ID is empty");
        }

        try {
            String jpql = "SELECT c FROM Card c WHERE c.account.accountNumber = :accountNumber and c.cardNumber = :cardNumber";
            Map<String, Object> params = new HashMap<>();
            params.put("accountNumber", accountNumber);
            params.put("cardNumber", cardNumber);

            Card card = cardGenericDao.findOneWithQuery(jpql, params);
            if (card == null) {
                LOGGER.error("No Card Exists");
                return CustomResponseEntity.error("No Card Exists");
            }

            LOGGER.info("Card Found, Updating Card Status....");
            card.setActive(status);
            card = cardGenericDao.saveOrUpdate(card);

            if (card != null) {
                LOGGER.info("Card Status has been Updated with Id: {}", card.getCardId());
                UserActivityRequest userActivity = new UserActivityRequest();
                userActivity.setActivityDate(LocalDateTime.now());
                userActivity.setCustomerId(card.getAccount().getCustomer());
                userActivity.setPkr(0.0);
                userActivity.setUserActivity("User Updated Card Status");

                userActivityService.saveUserActivity(userActivity);
                return new CustomResponseEntity<>("Card Status Updated Successfully");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return CustomResponseEntity.error("Unable to Update Card Status!");
        }

        return CustomResponseEntity.error("Unable to Update Card Status!");
    }

    @Override
    public CustomResponseEntity createCardRequest(CardRequestDto cardRequestDto) {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(addCardURL)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri.toString());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Generate card details
            int size = 16; // Example size
            String cardNumber = generateRandomNumber(size);

            int cvvSize = 4; // Example size
            String cvv = generateRandomNumber(cvvSize);

            // Set expiry date to 5 years from now
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 5);
            Date expiryDate = calendar.getTime();

            // Format expiry date to string if necessary
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedExpiryDate = outputFormat.format(expiryDate);

            CardApprovalResDto creditCardRequest = new CardApprovalResDto();
            creditCardRequest.setCardHolderName(cardRequestDto.getCardHolderName());
            creditCardRequest.setCardNumber(cardNumber);
            creditCardRequest.setCardType("1");
            creditCardRequest.setCvv(cvv);
            creditCardRequest.setExpiryDate(formattedExpiryDate); // Set formatted expiry date
            creditCardRequest.setAccountNumber(cardRequestDto.getAccountNumber());

            // Create HttpEntity with headers
            HttpEntity<CardApprovalResDto> entity = new HttpEntity<>(creditCardRequest, headers);

            // Make HTTP POST request
            ResponseEntity<CustomResponseEntity> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    CustomResponseEntity.class
            );

            // Handle response
            if (response.getStatusCode() == HttpStatus.OK) { // 200 status code
                CustomResponseEntity<Card> responseDto = response.getBody();
                if (responseDto != null) {
                    LOGGER.info("Received CustomerDto: " + responseDto.getMessage());
                    if (responseDto.isSuccess()) { // Card request successful
                        // Add Card
                        Card card = addCreditCard(creditCardRequest);
                        if (card == null) {
                            return CustomResponseEntity.error("Customer does not exist!");
                        }
                        // Request
                        CardRequest cardRequest = cardRequestMapper.dtoToEntity(cardRequestDto);
                        cardRequest.setRequestStatus("Approved");
                        cardRequest = requestRepository.save(cardRequest);

                        CustomResponseEntity customResponseEntity = new CustomResponseEntity();
                        customResponseEntity.setData(responseDto);
                        customResponseEntity.setMessage("Card has been generated");
                        customResponseEntity.setSuccess(true);
                        return customResponseEntity;
                    }
                } else {
                    // No customer found
                    return CustomResponseEntity.error("Unable to Process!");
                }
            } else {
                // Handle error response or non-200 status
                LOGGER.error("Unexpected response status: " + response.getStatusCode());
                return CustomResponseEntity.error("Unable to Process!");
            }

        } catch (Exception e) {
            // Handle exceptions
            LOGGER.error("Exception occurred: ", e);
            return CustomResponseEntity.error("Unable to Process!");
        }
        return null;
    }


    public static String generateRandomNumber(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    public Card addCreditCard(CardApprovalResDto card) {
            Card cardDetail = cardMapper.dtoJpe(card);

            // Get customer ID
            long customerId = accountRepository.findCustomerByAccountNumber(card.getAccountNumber());
            if (customerId == 0) {
                return null; // Customer does not exist
            }

            // Fetch account
            Account account = accountRepository.findByAccountNumber(card.getAccountNumber());

            // Create Customer object
            Customer customer = new Customer();
            customer.setId(customerId);

            // Set card details
            cardDetail.setActive(true);
            cardDetail.setCreatedAt(new Date());
            cardDetail.setAccount(account);

            // Set expiry date to 5 years from now
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 5);
            Date expiryDate = calendar.getTime();

            // Format the expiry date using SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedExpiryDate = sdf.format(expiryDate);

            // Set the formatted expiry date in cardDetail
            cardDetail.setExpiryDate(formattedExpiryDate);

            // Save the card detail
            cardDetail = cardRepository.save(cardDetail);

            // Log user activity
            UserActivityRequest userActivity = new UserActivityRequest();
            userActivity.setActivityDate(LocalDateTime.now());
//            userActivity.setCustomerId(account.getCustomer());
            userActivity.setPkr(0.0);
            userActivity.setUserActivity("User Requested For The Card");
            userActivityService.saveUserActivity(userActivity);

            return cardDetail;
        }

    @Override
    public CustomResponseEntity setPinDigiBankAndMyDatabase(String pin, String card) {

        if (!regex.isValidPin(pin)) {
            LOGGER.warn("Invalid PIN format: '{}'", pin);
            return CustomResponseEntity.error("Pin must be exactly 4 digits.");
        }
        LOGGER.info("Valid PIN format: '{}'", pin);
        try {
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
            URI uri = UriComponentsBuilder.fromHttpUrl(URLL)
                    .queryParam("pin", pin)
                    .queryParam("card", card)
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
            ResponseEntity<CustomResponseEntity> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CustomResponseEntity.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                CustomResponseEntity responseDto = response.getBody();
                if (responseDto != null) {
                    // Print or log responseDto to verify its content
                    LOGGER.info("Received CustomerDto: " + responseDto.toString());


                    String jpql = "SELECT c FROM Card c WHERE c.cardNumber = :card";
                    Map<String, Object> params = new HashMap<>();
                    params.put("card", card);

                    Card cardNo = cardGenericDao.findOneWithQuery(jpql, params);

                    if (cardNo == null) {
                        LOGGER.error("Card does not exists");
                        return CustomResponseEntity.error("Card does not exists");
                    }
                    if (cardNo.getPin() != null) {
                        LOGGER.error("Pin already exists");
                        return CustomResponseEntity.error("Pin already exists");
                    }
                    else
                    {
                        cardNo.setPin(pin);
                        cardNo.setCardNumber(card);
                        cardGenericDao.saveOrUpdate(cardNo);
                        return customResponseEntity = new CustomResponseEntity<>("Pin set Successfully!");
                    }
                } else
                    return null;
            } else {
                // Handle error response or non-200 status
                LOGGER.error("Unexpected response status: " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            // Handle exceptions
            LOGGER.error("Exception occurred: ", e);
        }
        return null;
    }


    @Override
    public CustomResponseEntity sendOtpForChangeCardPin(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
//            Customer customer = customerById.get();
            String email = customer.get().getEmail();

            // Create the OTP request DTO
            EmailOTPSendDto otpSendDto = new EmailOTPSendDto();
            otpSendDto.setEmail(email);
            otpSendDto.setReason("Change Card Pin");
            otpSendDto.setMobileNumber(customer.get().getMobileNumber());
            // Call the OTP service
            String sendOtp = "/createOTP";
            String otpServiceUrl = otpUrl + sendOtp;
            URI uri = UriComponentsBuilder.fromHttpUrl(otpServiceUrl)
                    .build()
                    .toUri();
            // Log the full request URL
            LOGGER.info("Request URL: " + uri.toString());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HttpEntity with headers
            HttpEntity<EmailOTPSendDto> entity = new HttpEntity<>(otpSendDto, headers);

            // Make HTTP GET request
            ResponseEntity<CustomResponseEntity> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    CustomResponseEntity.class
            );
            LOGGER.info(uri.toString() + "  " + response.toString());
            return response.getBody();
        }
        return CustomResponseEntity.error("Invalid Customer Id");
    }


    @Override
    public CustomResponseEntity changePin(ChangePinDto changePinRequestDto) {
        try {
            if(!regex.isValidPin(changePinRequestDto.getNewPin()) || !regex.isValidPin(changePinRequestDto.getOldPin())){
                LOGGER.warn("Invalid PIN format");
                return CustomResponseEntity.error("Pin must be exactly 4 digits.");
            }
            LOGGER.info("Valid PIN format");
            String cardNumber = changePinRequestDto.getCardNumber();
            String newPin = changePinRequestDto.getNewPin();
            String confirmNewPin = changePinRequestDto.getConfirmNewPin();

            if (!newPin.equals(confirmNewPin)) {
                LOGGER.error("New PIN and confirmation PIN do not match");
                return CustomResponseEntity.error("New PIN and confirmation PIN do not match");
            }

            Optional<Card> optionalCard = cardRepository.findByCardNumber(cardNumber);
            if (!optionalCard.isPresent()) {
                LOGGER.error("Card not found");
                return CustomResponseEntity.error("Card not found");
            }

            Card card = optionalCard.get();
            if (!card.getPin().equals(changePinRequestDto.getOldPin())) {
                return CustomResponseEntity.error("Old PIN is incorrect");
            }

            // Check if new PIN is the same as the old PIN
            if (newPin.equals(changePinRequestDto.getOldPin())) {
                LOGGER.error("New PIN matches the old PIN for card number {}", cardNumber);
                return CustomResponseEntity.error("New PIN and old PIN matched. Try a different PIN other than the old PIN.");
            }

            card.setPin(newPin);
            cardRepository.save(card);

            LOGGER.info("PIN successfully changed for card with ID {}", card.getCardId());
            return customResponseEntity = new CustomResponseEntity<>("PIN changed successfully");

        } catch (Exception e) {
            LOGGER.error("Error occurred while changing PIN: ", e);
            return CustomResponseEntity.error("Unable to change PIN");
        }
    }
}
