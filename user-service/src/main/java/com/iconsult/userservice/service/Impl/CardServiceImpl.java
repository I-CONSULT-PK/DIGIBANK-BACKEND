package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.model.dto.request.CardDto;
import com.iconsult.userservice.model.dto.request.CardRequestDto;
import com.iconsult.userservice.model.dto.response.CardApprovalResDto;
import com.iconsult.userservice.model.dto.response.CardResponseDto;
import com.iconsult.userservice.model.entity.Card;
import com.iconsult.userservice.model.entity.CardRequest;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.mapper.CardMapper;
import com.iconsult.userservice.model.mapper.CardRequestMapper;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.CardRepository;
import com.iconsult.userservice.repository.CardRequestRepository;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.service.CardService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
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
import java.security.cert.X509Certificate;
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

    @Autowired
    private GenericDao<Customer> customerGenericDao;

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
    private CardMapper cardMapper;
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

                    // Finding the card by cardNumber
                    Card duplicateCard = customer.getCardList().stream()
                            .filter(c -> cardDto.getCardNumber().equals(c.getCardNumber()))
                            .findFirst()  // Gets the first match or an empty Optional
                            .orElse(null); // Returns null if no match is found

                    if (duplicateCard != null) {
                        LOGGER.error("Card Already Exists");
                        return CustomResponseEntity.error("Card Already Exists");
                    }

                    if (customer == null) return CustomResponseEntity.error("Customer Does not exist");

                    Map<String, Object> dataMap = (Map<String, Object>) responseDto.getData();
                    Card card = new Card();
                    card.setCustomer(customer);
                    card.setCardNumber((String) dataMap.get("cardNumber"));
                    card.setCardHolderName((String) dataMap.get("cardHolderName"));
                    card.setCardType((String) dataMap.get("cardType"));
                    card.setCvv((String) dataMap.get("cvv"));
                    card.setActive((Boolean) dataMap.get("active"));
                    card.setExpiryDate((String) dataMap.get("expiryDate"));
                    card.setIsCreditCard((Boolean) dataMap.get("isCreditCard"));

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
    public CustomResponseEntity getAllCardById(Long id) {
        Optional<Customer> byId = customerRepository.findById(id);

        List<Card> listOfCard = byId.get().getCardList();
        CardResponseDto cardResponseDto = new CardResponseDto();
        cardResponseDto.setId(byId.get().getId());
        cardResponseDto.setCards(listOfCard);

        return new CustomResponseEntity<>(cardResponseDto, "Card List");
    }

    @Override
    public CustomResponseEntity updateCardStatus(Long cardNumber, Long customerID, Boolean status) {

        if (cardNumber == null) {
            LOGGER.error("Card Number is Null");
            return CustomResponseEntity.error("Card Number is empty");
        } else if (customerID == null) {
            LOGGER.error("Customer ID is Null");
            return CustomResponseEntity.error("Customer ID is empty");
        }

        try {
            String jpql = "SELECT c FROM Card c WHERE c.customer.id = :customerID and c.cardNumber = :cardNumber";
            Map<String, Object> params = new HashMap<>();
            params.put("customerID", customerID);
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
            //setCard approval request for http POST request

            int size = 10; // Example size
            String cardNumber = generateRandomNumber(size);

            int cvvSize = 4; // Example size
            String cvv = generateRandomNumber(cvvSize);

            CardApprovalResDto creditCardRequest = new CardApprovalResDto();
            creditCardRequest.setCardHolderName(cardRequestDto.getCardHolderName());
            creditCardRequest.setCardNumber(cardNumber);
            creditCardRequest.setCardType("1");
            creditCardRequest.setCvv(cvv);
            creditCardRequest.setExpiryDate(cardRequestDto.getExpireDate());
            creditCardRequest.setAccountNumber(cardRequestDto.getAccountNumber());
            // Create HttpEntity with headers
            HttpEntity<CardApprovalResDto> entity = new HttpEntity<>(creditCardRequest ,headers);

            // Make HTTP GET request
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
                    // Print or log responseDto to verify its content
                    LOGGER.info("Received CustomerDto: " + responseDto.getMessage());
                    if (responseDto.isSuccess()){//card request work
                        //AddCard
                        Card card = addCreditCard(creditCardRequest);
                        if(card == null){
                            return CustomResponseEntity.error("Customer does not exist!");
                        }
                        //request
                        CardRequest cardRequest = cardRequestMapper.dtoToEntity(cardRequestDto);
                        cardRequest.setRequestStatus("Approved");
                        cardRequest = requestRepository.save(cardRequest);

                        CustomResponseEntity customResponseEntity = new CustomResponseEntity();
                        customResponseEntity.setData(responseDto);
                        customResponseEntity.setMessage("Card has been generated");
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

    public Card addCreditCard(CardApprovalResDto card){
        Card cardDetail = cardMapper.dtoJpe(card);
        long customerId = accountRepository.findCustomerByAccountNumber(card.getAccountNumber());
        if(customerId == 0){
            return null;
        }

        Customer customer = new Customer();
        customer.setId(customerId);
        cardDetail.setCustomer(customer);
        cardDetail.setActive(true);
        cardDetail.setCreatedAt(new Date());

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime dateTime = LocalDateTime.parse(card.getExpiryDate().toString(), inputFormatter);

        String outputDate = dateTime.format(outputFormatter);
        cardDetail.setExpiryDate(outputDate);
        cardDetail = cardRepository.save(cardDetail);
        return cardDetail;
    }
}
