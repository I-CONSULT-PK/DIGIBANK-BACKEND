package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.constant.UtilityType;
import com.iconsult.userservice.model.dto.request.BillPaymentDto;
import com.iconsult.userservice.model.dto.request.ScheduleBillPaymentRequest;
import com.iconsult.userservice.model.dto.response.BillPaymentTransactionDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.AccountCDDetails;
import com.iconsult.userservice.model.entity.ScheduleBillPayment;
import com.iconsult.userservice.model.entity.Transactions;
import com.iconsult.userservice.model.mapper.BillPaymentTransactionMapper;
import com.iconsult.userservice.repository.*;
import com.iconsult.userservice.service.BillPaymentService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BillPaymentServiceImpl implements BillPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillPaymentServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private GenericDao<Transactions> transactionsGenericDao;

    @Autowired
    private AccountCDDetailsRepository accountCDDetailsRepository;

    private final String URL = "http://localhost:8078/v1/billpayment/getBillDetails";
    private final String billersURL = "http://localhost:8078/v1/billpayment/getBillers";
    private final String utilitiesURL = "http://localhost:8078/v1/billpayment/getUtilityTypes";

    @Autowired
    private SchdeuledBillPaymentRepository schdeuledBillPaymentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public CustomResponseEntity<Object> getUtilityDetails(String consumerNumber, String serviceCode, String utilityType, BillPaymentDto billPaymentDto) {
        try {
            // Build URL with query parameters
            URI uri = UriComponentsBuilder.fromHttpUrl(URL)
                    .queryParam("consumerNumber", consumerNumber)
                    .queryParam("serviceCode", serviceCode)
                    .queryParam("utilityType", utilityType)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: {}", uri);

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

            // Extract the response body
            CustomResponseEntity<?> responseBody = response.getBody();

            if (responseBody != null) {
                // Check if the response indicates an error
                if (!responseBody.isSuccess()) {
                    String errorMessage = responseBody.getMessage(); // Get error message from response body
                    LOGGER.error("Error from response: {}", errorMessage);
                    throw new RuntimeException("Error from response: " + errorMessage);
                }

                // Process the response body
                Map<String, Object> data = (Map<String, Object>) responseBody.getData();
                Object billObject = data.get("bill");

                if (billObject instanceof Map) {
                    Map<String, Object> billMap = (Map<String, Object>) billObject;

                    Object amountObject = billMap.get("amount");
                    Object afterDueDateAmountObject = billMap.get("amountDueAfterDueDate");
                    String dueDateString = (String) billMap.get("dueDate");
                    String referenceNumber = (String) billMap.get("referenceNumber");
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    // Convert amountObject to a Double
                    Double amount = convertToDouble(amountObject);
                    Double afterDueDateAmount = convertToDouble(afterDueDateAmountObject);

                    // Validate the amount
                    if (amount == null || amount <= 0) {
                        throw new RuntimeException("Invalid amount: " + amount);
                    }

                    // Fetch account details
                    Account account = accountRepository.findByAccountNumber(billPaymentDto.getAccountNumber());

                    if (account != null) {
                        Double accountBalance = account.getAccountBalance();
                        Double singleDayBillPayLimit = account.getSingleDayBillPayLimit();
                        // 1. Fetch today's transactions and sum their debit amounts
//                        LocalDate today2 = LocalDate.now();
                        Double totalDebitToday = calculateTodayTotalDebit(account.getId());

                        // 2. Check if the new transaction amount plus today's total exceeds the limit
                        if (totalDebitToday + amount > singleDayBillPayLimit) {
                            LOGGER.warn("Daily transaction limit exceeded for account number: {}", billPaymentDto.getAccountNumber());
                            return new CustomResponseEntity<>(1002, "Daily transaction limit exceeded");
                        }


                        // Check if account balance is sufficient
                        if (amount <= accountBalance) {
                            // Check due date and select the amount to deduct
                            if (dueDateString != null) {
                                LocalDate dueDate = LocalDate.parse(dueDateString); // Parse dueDateString to LocalDate
                                LocalDate today = LocalDate.now();

                                if (dueDate.isBefore(today)) {
                                    // Due date has passed, use afterDueDateAmount
                                    amount = afterDueDateAmount != null ? afterDueDateAmount : amount;
                                }
                            }

                            if (accountBalance >= amount) {
                                // Deduct amount from account balance
                                Double previousBalance = accountBalance; // Save the previous balance
                                accountBalance -= amount;

                                // Update account balance
                                account.setAccountBalance(accountBalance);

                                // Create and set up the transaction DTO
                                BillPaymentTransactionDto transactionDto = new BillPaymentTransactionDto();
                                transactionDto.setAccountNumber(account.getAccountNumber());
                                transactionDto.setDebitAmount(amount);
                                transactionDto.setCreditAmount(0.0);
                                transactionDto.setCurrentBalance(accountBalance);
                                transactionDto.setTransactionId(referenceNumber);
                                transactionDto.setTransactionDate(formatter.format(date));
                                transactionDto.setIbanCode(account.getCustomer().getAccountNumber());
                                transactionDto.setTransactionNarration("Bill Payment Against Consumer Number " + data.get("accountNumber"));
                                transactionDto.setTransactionType("BILL");

                                // Convert DTO to entity
                                Transactions transaction = BillPaymentTransactionMapper.toEntity(transactionDto, account);

                                // Update AccountCDDetails
                                AccountCDDetails updateLastDebitAmount = accountCDDetailsRepository.findByAccount_Id(account.getId());
                                if (updateLastDebitAmount != null) {
                                    updateLastDebitAmount.setDebit(amount);
                                    updateLastDebitAmount.setAccount(account);
                                    updateLastDebitAmount.setActualBalance(accountBalance);
                                    updateLastDebitAmount.setPreviousBalance(previousBalance); // Set the previous balance
                                    updateLastDebitAmount.setCredit(0.0);
                                } else {
                                    // Handle the case where AccountCDDetails is not found
                                    updateLastDebitAmount = new AccountCDDetails();
                                    updateLastDebitAmount.setDebit(amount);
                                    updateLastDebitAmount.setAccount(account);
                                    updateLastDebitAmount.setActualBalance(accountBalance);
                                    updateLastDebitAmount.setPreviousBalance(previousBalance);
                                    updateLastDebitAmount.setCredit(0.0);
                                }
                                accountCDDetailsRepository.save(updateLastDebitAmount);

                                // Save the updated account and transaction back to the database
                                accountRepository.save(account);
                                transactionsGenericDao.saveOrUpdate(transaction);

                                LOGGER.info("Account balance and transaction updated successfully");

                                // Prepare the response data with only the processed amount
                                Map<String, Object> processedData = new HashMap<>();
                                processedData.put("processedAmount", amount);
                                processedData.put("referenceNumber", referenceNumber);
                                processedData.put("date", formatter.format(date));
                                processedData.put("consumerNumber", consumerNumber);

                                // Return the successful response body
                                return new CustomResponseEntity<>(processedData, "Utility Bill processed successfully");
                            } else {
                                return CustomResponseEntity.error("Insufficient balance for the transaction..");
                            }
                        } else {
                            // Handle case where account balance is not sufficient
                            LOGGER.warn("Insufficient balance for account number: {}", billPaymentDto.getAccountNumber());
                            return new CustomResponseEntity<>(1001, "Insufficient balance for the transaction");
                        }
                    } else {
                        LOGGER.warn("Account not found for account number: {}", billPaymentDto.getAccountNumber());
                        throw new RuntimeException("Account not found");
                    }

                } else {
                    LOGGER.warn("Bill object is not a Map");
                    throw new RuntimeException("Invalid bill format");
                }
            } else {
                // Handle case where response body is null
                LOGGER.error("Response body is null");
                throw new RuntimeException("Response body is null");
            }

        } catch (Exception e) {
            LOGGER.error("An error occurred while getting utility details", e);

            // Create an error response
            return CustomResponseEntity.errorResponse(e);
        }
    }

    @Override
    public CustomResponseEntity<Object> schdeuleUtilityBillPay(ScheduleBillPaymentRequest scheduleBillPaymentRequest) {
        try {
            // Build URL with query parameters
            URI uri = UriComponentsBuilder.fromHttpUrl(URL)
                    .queryParam("consumerNumber", scheduleBillPaymentRequest.getConsumerNumber())
                    .queryParam("serviceCode", scheduleBillPaymentRequest.getServiceCode())
                    .queryParam("utilityType", scheduleBillPaymentRequest.getUtilityType())
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: {}", uri);

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

            // Extract the response body
            CustomResponseEntity<?> responseBody = response.getBody();

            if (responseBody != null) {
                // Check if the response indicates an error
                if (!responseBody.isSuccess()) {
                    String errorMessage = responseBody.getMessage(); // Get error message from response body
                    LOGGER.error("Error from response: {}", errorMessage);
                    throw new RuntimeException("Error from response: " + errorMessage);
                }

                // Process the response body
                Map<String, Object> data = (Map<String, Object>) responseBody.getData();
                Object billObject = data.get("bill");

                if (billObject instanceof Map) {
                    Map<String, Object> billMap = (Map<String, Object>) billObject;

                    Object amountObject = billMap.get("amount");
                    Object afterDueDateAmountObject = billMap.get("amountDueAfterDueDate");
                    String dueDateString = (String) billMap.get("dueDate");
                    String referenceNumber = (String) billMap.get("referenceNumber");
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    // Convert amountObject to a Double
                    Double amount = convertToDouble(amountObject);
                    Double afterDueDateAmount = convertToDouble(afterDueDateAmountObject);

                    // Validate the amount
                    if (amount == null || amount <= 0) {
                        throw new RuntimeException("Invalid amount: " + amount);
                    }

                    // Fetch account details
                    Account account = accountRepository.findByAccountNumber(scheduleBillPaymentRequest.getAccountNumber());

                    if (account != null) {
                        Double accountBalance = account.getAccountBalance();
                        Double singleDayBillPayLimit = account.getSingleDayBillPayLimit();
                        // 1. Fetch today's transactions and sum their debit amounts
//                        LocalDate today2 = LocalDate.now();
                        Double totalDebitToday = calculateTodayTotalDebit(account.getId());

                        // 2. Check if the new transaction amount plus today's total exceeds the limit
                        if (totalDebitToday + amount > singleDayBillPayLimit) {
                            LOGGER.warn("Daily transaction limit exceeded for account number: {}", scheduleBillPaymentRequest.getAccountNumber());
                            return new CustomResponseEntity<>(1002, "Daily transaction limit exceeded");
                        }


                        // Check if account balance is sufficient
                        if (amount <= accountBalance) {
                            // Check due date and select the amount to deduct
                            if (dueDateString != null) {
                                LocalDate dueDate = LocalDate.parse(dueDateString); // Parse dueDateString to LocalDate
                                LocalDate today = LocalDate.now();

                                if (dueDate.isBefore(today)) {
                                    // Due date has passed, use afterDueDateAmount
                                    amount = afterDueDateAmount != null ? afterDueDateAmount : amount;
                                }
                            }

                            if (accountBalance >= amount) {
                                // Deduct amount from account balance
                                Double previousBalance = accountBalance; // Save the previous balance
                                accountBalance -= amount;

                                // Update account balance
                                account.setAccountBalance(accountBalance);

                                // Create and set up the transaction DTO
                                BillPaymentTransactionDto transactionDto = new BillPaymentTransactionDto();
                                transactionDto.setAccountNumber(account.getAccountNumber());
                                transactionDto.setDebitAmount(amount);
                                transactionDto.setCreditAmount(0.0);
                                transactionDto.setCurrentBalance(accountBalance);
                                transactionDto.setTransactionId(referenceNumber);
                                transactionDto.setTransactionDate(formatter.format(date));
                                transactionDto.setIbanCode(account.getCustomer().getAccountNumber());
                                transactionDto.setTransactionNarration("Bill Payment Against Consumer Number " + data.get("accountNumber"));
                                transactionDto.setTransactionType("BILL");

                                // Convert DTO to entity
                                Transactions transaction = BillPaymentTransactionMapper.toEntity(transactionDto, account);

                                // Update AccountCDDetails
                                AccountCDDetails updateLastDebitAmount = accountCDDetailsRepository.findByAccount_Id(account.getId());
                                if (updateLastDebitAmount != null) {
                                    updateLastDebitAmount.setDebit(amount);
                                    updateLastDebitAmount.setAccount(account);
                                    updateLastDebitAmount.setActualBalance(accountBalance);
                                    updateLastDebitAmount.setPreviousBalance(previousBalance); // Set the previous balance
                                    updateLastDebitAmount.setCredit(0.0);
                                } else {
                                    // Handle the case where AccountCDDetails is not found
                                    updateLastDebitAmount = new AccountCDDetails();
                                    updateLastDebitAmount.setDebit(amount);
                                    updateLastDebitAmount.setAccount(account);
                                    updateLastDebitAmount.setActualBalance(accountBalance);
                                    updateLastDebitAmount.setPreviousBalance(previousBalance);
                                    updateLastDebitAmount.setCredit(0.0);
                                }
                                accountCDDetailsRepository.save(updateLastDebitAmount);

                                // Save the updated account and transaction back to the database
                                accountRepository.save(account);
                                transactionsGenericDao.saveOrUpdate(transaction);

                                LOGGER.info("Account balance and transaction updated successfully");

                                // Prepare the response data with only the processed amount
                                Map<String, Object> processedData = new HashMap<>();
                                processedData.put("processedAmount", amount);
                                processedData.put("referenceNumber", referenceNumber);
                                processedData.put("date", formatter.format(date));
                                processedData.put("consumerNumber", scheduleBillPaymentRequest.getConsumerNumber());
                                ScheduleBillPayment billPayment = schdeuledBillPaymentRepository.findById(scheduleBillPaymentRequest.getScheduledId()).orElse(null);
                                billPayment.setProcessAmount(String.valueOf(amount));
                                billPayment.setReferenceNumber(referenceNumber);
                                billPayment.setStatus("Completed");
                                schdeuledBillPaymentRepository.save(billPayment);

                            } else {
                                ScheduleBillPayment billPayment = schdeuledBillPaymentRepository.findById(scheduleBillPaymentRequest.getScheduledId()).orElse(null);
                                billPayment.setStatus("In-Completed");
                                schdeuledBillPaymentRepository.save(billPayment);
                                LOGGER.error("Local accounts not found.");
                                throw new SecurityException("Local accounts not found.");
                            }
                        } else {
                            // Handle case where account balance is not sufficient
                            ScheduleBillPayment billPayment = schdeuledBillPaymentRepository.findById(scheduleBillPaymentRequest.getScheduledId()).orElse(null);
                            billPayment.setStatus("In-Completed");
                            schdeuledBillPaymentRepository.save(billPayment);
                            LOGGER.warn("Insufficient balance for account number: {}", scheduleBillPaymentRequest.getAccountNumber());
                            throw new SecurityException("Insufficient balance.");

                        }
                    } else {
                        ScheduleBillPayment billPayment = schdeuledBillPaymentRepository.findById(scheduleBillPaymentRequest.getScheduledId()).orElse(null);
                        billPayment.setStatus("In-Completed");
                        schdeuledBillPaymentRepository.save(billPayment);
                        LOGGER.warn("Account not found for account number: {}", scheduleBillPaymentRequest.getAccountNumber());
                        throw new SecurityException("Insufficient balance.");
                    }

                } else {
                    ScheduleBillPayment billPayment = schdeuledBillPaymentRepository.findById(scheduleBillPaymentRequest.getScheduledId()).orElse(null);
                    billPayment.setStatus("In-Completed");
                    schdeuledBillPaymentRepository.save(billPayment);
                    LOGGER.warn("Bill object is not a Map");
                    throw new SecurityException("Bill object is not a Map.");
                }
            } else {
                // Handle case where response body is null
                ScheduleBillPayment billPayment = schdeuledBillPaymentRepository.findById(scheduleBillPaymentRequest.getScheduledId()).orElse(null);
                billPayment.setStatus("In-Completed");
                schdeuledBillPaymentRepository.save(billPayment);
                LOGGER.warn("Response Body Is Null");
                throw new SecurityException("Response Body Is Null");
            }

        } catch (Exception e) {
            ScheduleBillPayment billPayment = schdeuledBillPaymentRepository.findById(scheduleBillPaymentRequest.getScheduledId()).orElse(null);
            billPayment.setStatus("In-Completed");
            schdeuledBillPaymentRepository.save(billPayment);
            LOGGER.error("An error occurred while getting utility details", e);
            throw new SecurityException("Unable to Process!");

            // Create an error response
        }
        return null;
    }

    @Override
    public CustomResponseEntity getAllBillProviders(String utilityType) {

        UtilityType utilityType1 = null;
        try {
            utilityType1 = UtilityType.valueOf(utilityType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CustomResponseEntity.error("Invalid value for utilityType. Please use one of the following values: " +
                    "INTERNET, CREDIT_CARD, WATER, GAS, PTCL, ELECTRICITY : " + utilityType);
        }
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(billersURL)
                    .queryParam("utilityType", utilityType1)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: {}", uri);

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
            CustomResponseEntity<?> responseBody = response.getBody();
            if (responseBody != null) {

                if (!responseBody.isSuccess()) {
                    String errorMessage = responseBody.getMessage();
                    LOGGER.error("Error from response: {}", errorMessage);
                    return CustomResponseEntity.error("Error from response: " + errorMessage);
                }

                Map<String, Object> billerMap = (Map<String, Object>) responseBody.getData();


                return new CustomResponseEntity(billerMap, "Billers List");

            } else {
                LOGGER.error("Response body is null");
                return CustomResponseEntity.error("Response body is null");
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred while getting billers details", e);
            return CustomResponseEntity.errorResponse(e);
        }

    }

    @Override
    public CustomResponseEntity getUtilityTypes() {

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(utilitiesURL)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: {}", uri);

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
            CustomResponseEntity<?> responseBody = response.getBody();
            if (responseBody != null) {

                if (!responseBody.isSuccess()) {
                    String errorMessage = responseBody.getMessage();
                    LOGGER.error("Error from response: {}", errorMessage);
                    return CustomResponseEntity.error("Error from response: " + errorMessage);
                }

                Object data = responseBody.getData();
                if (data instanceof List) {
                    List<?> dataList = (List<?>) data;

                    // Create a new Map to wrap the list under a single key
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("utility", dataList);

                    // Return the wrapped list
                    return new CustomResponseEntity(responseMap, "Utility List");
                }

                // If data is not a list, return as-is
                return new CustomResponseEntity(data, "Utility List");

            } else {
                LOGGER.error("Response body is null");
                return CustomResponseEntity.error("Response body is null");
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred while fetching utility types!", e);
            return CustomResponseEntity.errorResponse(e);
        }
    }


    private Double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }

    private Double calculateTodayTotalDebit(Long accountId) {
        LocalDate today = LocalDate.now();
        List<Transactions> todayTransactions = transactionRepository.findByAccount_IdAndTransactionTypeAndTransactionDateContaining(accountId, "BILL", today.toString());
        return todayTransactions.stream()
                .mapToDouble(Transactions::getDebitAmt)
                .sum();
    }

/*    private boolean isTransactionLimitExceeded(Double currentTotal, Double transactionAmount, Double limit) {
        return currentTotal + transactionAmount > limit;
    }*/


}