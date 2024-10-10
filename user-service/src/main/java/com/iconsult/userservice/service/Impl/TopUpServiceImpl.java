package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.model.dto.response.TopPackageTransactionDto;
import com.iconsult.userservice.model.dto.response.TopUpPaymentTransactionDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.AccountCDDetails;
import com.iconsult.userservice.model.entity.ScheduleMobileTopUpPayment;
import com.iconsult.userservice.model.entity.Transactions;
import com.iconsult.userservice.model.mapper.TopUpPackageTransactionMapper;
import com.iconsult.userservice.model.mapper.TopUpTransactionMapper;
import com.iconsult.userservice.repository.AccountCDDetailsRepository;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.ScheduleMobileTopUpPaymentRepository;
import com.iconsult.userservice.repository.TransactionRepository;
import com.iconsult.userservice.service.TopUpService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.*;

@Service
public class TopUpServiceImpl implements TopUpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopUpServiceImpl.class);
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private GenericDao<Transactions> transactionsGenericDao;

    @Autowired
    AccountCDDetailsRepository accountCDDetailsRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    ScheduleMobileTopUpPaymentRepository scheduleMobileTopUpPaymentRepository;

    private String URL = "http://localhost:8089/v1/topup/topUpTransaction";
    private String PackageURL = "http://localhost:8089/v1/packages/all";

    private String BundleTransactionURL= "http://localhost:8089/v1/packages/subscribePackage";


    @Override
    public CustomResponseEntity getMobileNumberAndPlanDetail(String phoneNumber, Double amount, String carrier, String plan, String accountNumber ) {
        // Fetch account details from repository
        Account fetchAccountNumber;
        try {
            fetchAccountNumber = accountRepository.findByAccountNumber(accountNumber);
            if (fetchAccountNumber == null) {
                return CustomResponseEntity.error("Enter Correct Account Number");
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching account details: {}", e.getMessage());
            return CustomResponseEntity.error("Error fetching account details");
        }

        // Build URI with query parameters
        URI uri;
        try {
            uri = UriComponentsBuilder.fromHttpUrl(URL)
                    .queryParam("phoneNumber", phoneNumber)
                    .queryParam("amount", amount)
                    .queryParam("carrier", carrier)
                    .queryParam("plan", plan)
                    .build()
                    .toUri();
        } catch (Exception e) {
            LOGGER.error("Error building URI: {}", e.getMessage());
            return CustomResponseEntity.error("Error building URI");
        }

        // Log the full request URL
        LOGGER.info("Request URL: {}", uri);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make HTTP POST request
        ResponseEntity<CustomResponseEntity> response;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    CustomResponseEntity.class
            );
        } catch (Exception e) {
            LOGGER.error("Error making HTTP request: {}", e.getMessage());
            return CustomResponseEntity.error("External Service Response Error");
        }

        // Extract the response body
        CustomResponseEntity<?> responseBody = response.getBody();
        if (responseBody == null) {
            LOGGER.error("Response body is null");
            return CustomResponseEntity.error("Response body is null");
        }

        // Check if the response indicates an error
        if (!responseBody.isSuccess()) {
            String errorMessage = responseBody.getMessage(); // Get error message from response body
            LOGGER.error("Error from response: {}", errorMessage);
            return CustomResponseEntity.error("Error from response: " + errorMessage);
        }

        // Extract amount from response data
        Map<String, Object> data;
        Object billObject;
        Double billAmount;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            data = (Map<String, Object>) responseBody.getData();
            billObject = data.get("amount");
            if (billObject instanceof Number) {
                billAmount = ((Number) billObject).doubleValue();
            } else {
                LOGGER.error("Invalid amount received in response: {}", billObject);
                return CustomResponseEntity.error("Invalid amount received in response");
            }
        } catch (Exception e) {
            LOGGER.error("Error processing response data: {}", e.getMessage());
            return CustomResponseEntity.error("Error processing response data");
        }

        // Deduct billAmount from account balance
        Double accountBalance = fetchAccountNumber.getAccountBalance();
        if (accountBalance == null) {
            accountBalance = 0.0;
        }

        if (accountBalance < billAmount) {
            LOGGER.error("Insufficient balance. Current balance: {}, Required amount: {}", accountBalance, billAmount);
            return CustomResponseEntity.error("Insufficient balance");
        }

        // Record the balance before the deduction
        Double previousBalance = accountBalance;

        // Perform the deduction
        Double newBalance = accountBalance - billAmount;

        // Create transaction DTO
        TopUpPaymentTransactionDto transactionDto = new TopUpPaymentTransactionDto();
        transactionDto.setAccountNumber(fetchAccountNumber.getAccountNumber());
        transactionDto.setDebitAmount(amount);
        transactionDto.setCreditAmount(0.0);
        transactionDto.setCurrentBalance(newBalance);
        transactionDto.setTransactionId(generateRandomReference());
        transactionDto.setTransactionDate(formatter.format(new Date()));
        transactionDto.setIbanCode(fetchAccountNumber.getIbanCode());
        transactionDto.setTransactionNarration("Top-up transaction");

        // Map DTO to entity
        Transactions transaction;
        try {
            transaction = TopUpTransactionMapper.toEntity(transactionDto, fetchAccountNumber);
        } catch (Exception e) {
            LOGGER.error("Error mapping DTO to entity: {}", e.getMessage());
            return CustomResponseEntity.error("Error mapping DTO to entity");
        }

        // Update or create AccountCDDetails
        AccountCDDetails updateLastDebitAmount;
        try {
            updateLastDebitAmount = accountCDDetailsRepository.findByAccount_Id(fetchAccountNumber.getId());
            if (updateLastDebitAmount != null) {
                // Update existing AccountCDDetails
                updateLastDebitAmount.setDebit(amount);
                updateLastDebitAmount.setAccount(fetchAccountNumber);
                updateLastDebitAmount.setActualBalance(newBalance); // Set the current balance
                updateLastDebitAmount.setPreviousBalance(previousBalance); // Balance before deduction
                updateLastDebitAmount.setCredit(0.0);
            } else {
                // Create new AccountCDDetails if not found
                updateLastDebitAmount = new AccountCDDetails();
                updateLastDebitAmount.setDebit(amount);
                updateLastDebitAmount.setAccount(fetchAccountNumber);
                updateLastDebitAmount.setActualBalance(newBalance); // Set the current balance
                updateLastDebitAmount.setPreviousBalance(previousBalance); // Balance before deduction
                updateLastDebitAmount.setCredit(0.0);
            }
            accountCDDetailsRepository.save(updateLastDebitAmount);
        } catch (Exception e) {
            LOGGER.error("Error updating or creating AccountCDDetails: {}", e.getMessage());
            return CustomResponseEntity.error("Error updating or creating AccountCDDetails");
        }

        // Save transaction
        try {
            transactionsGenericDao.saveOrUpdate(transaction);
        } catch (Exception e) {
            LOGGER.error("Error saving transaction: {}", e.getMessage());
            return CustomResponseEntity.error("Error saving transaction");
        }

        // Update account balance in repository
        try {
            fetchAccountNumber.setAccountBalance(newBalance);
            accountRepository.save(fetchAccountNumber);
        } catch (Exception e) {
            LOGGER.error("Error updating account balance: {}", e.getMessage());
            return CustomResponseEntity.error("Error updating account balance");
        }

        return new CustomResponseEntity(responseBody, "Request successfully processed");
    }

    @Override
    public CustomResponseEntity getAllNetworkPackages() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make HTTP GET request
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    PackageURL,
                    HttpMethod.GET,
                    entity,
                    String.class // Retrieve response as String
            );
        } catch (Exception e) {
            LOGGER.error("Error making HTTP request: {}", e.getMessage());
            return CustomResponseEntity.error("Error making HTTP request");
        }

        // Extract the response body
        String responseBody = response.getBody();
        if (responseBody == null) {
            LOGGER.error("Response body is null");
            return CustomResponseEntity.error("Response body is null");
        }

        // Process response data
        List<Map<String, Object>> networkPackages = parseJsonArray(responseBody);

        return new CustomResponseEntity(networkPackages, "Request successfully processed");
    }
    @Override
    public CustomResponseEntity packageTransaction(Long packageId, String accountNumber, String mobileNumber) {
        // Check if account exists
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            return CustomResponseEntity.error("Account doesn't exist");
        }

        // Build URI with query parameters
        URI uri;
        try {
            uri = UriComponentsBuilder.fromHttpUrl(BundleTransactionURL)
                    .queryParam("packageId", packageId)
                    .queryParam("mobileNumber", mobileNumber)
                    .build()
                    .toUri();
        } catch (Exception e) {
            LOGGER.error("Error building URI: {}", e.getMessage());
            return CustomResponseEntity.error("Error building URI");
        }

        // Log the full request URL
        LOGGER.info("Request URL: {}", uri);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make HTTP GET request
        ResponseEntity<CustomResponseEntity> response;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CustomResponseEntity.class
            );
        } catch (Exception e) {
            LOGGER.error("Error making HTTP request: {}", e.getMessage());
            return CustomResponseEntity.error("Error making HTTP request");
        }

        // Process the response
        CustomResponseEntity<?> responseBody = response.getBody();
        if (responseBody == null) {
            LOGGER.error("Response body is null");
            return CustomResponseEntity.error("Response body is null");
        }

        if (!responseBody.isSuccess()) {
            String errorMessage = responseBody.getMessage(); // Get error message from response body
            LOGGER.error("Error from response: {}", errorMessage);
            return CustomResponseEntity.error(errorMessage);
        }

        // Extract amount from response data
        Double billAmount;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseBody.getData();
            Object billObject = data.get("package details");

            if (billObject instanceof Map) {
                Map<String,Object> topUpMap = (Map<String,Object>) billObject;
                Object priceObject = topUpMap.get("price");
                billAmount = ((Number) priceObject).doubleValue();
            } else {
                LOGGER.error("Invalid amount received in response: {}", billObject);
                return CustomResponseEntity.error("Invalid amount received in response");
            }
        } catch (Exception e) {
            LOGGER.error("Error processing response data: {}", e.getMessage());
            return CustomResponseEntity.error("Error processing response data");
        }

        // Check account balance
        Double accountBalance = account.getAccountBalance();
        if (accountBalance == null) {
            accountBalance = 0.0;
        }
//        Double singleDayTopUpLimit = account.getSingleDayBillPayLimit();
        Double singleDayTopUpLimit = account.getSingleDayTopUpLimit();
        // 1. Fetch today's transactions and sum their debit amounts
//                        LocalDate today2 = LocalDate.now();
        Double totalDebitToday = calculateTodayTotalDebit(account.getId());

        // 2. Check if the new transaction amount plus today's total exceeds the limit
        if (totalDebitToday + billAmount > singleDayTopUpLimit) {
            LOGGER.warn("Daily transaction limit exceeded for account number: {}", account.getAccountNumber());
            return new CustomResponseEntity<>(1002, "Daily transaction limit exceeded ....");
        }

        if (accountBalance < billAmount) {
            LOGGER.error("Insufficient balance. Current balance: {}, Required amount: {}", accountBalance, billAmount);
            return CustomResponseEntity.error("Insufficient balance");
        }

        // Record the balance before the deduction
        Double previousBalance = accountBalance;

        // Perform the deduction
        Double newBalance = accountBalance - billAmount;

        // Create transaction DTO
        TopPackageTransactionDto transactionDto = new TopPackageTransactionDto();
        transactionDto.setAccountNumber(account.getAccountNumber());
        transactionDto.setDebitAmount(billAmount);
        transactionDto.setCreditAmount(0.0);
        transactionDto.setCurrentBalance(newBalance);
        transactionDto.setTransactionId(generateRandomReference());
        transactionDto.setTransactionDate(formatter.format(new Date()));
        transactionDto.setIbanCode(account.getIbanCode());
        transactionDto.setTransactionNarration("Top-up transaction");
        transactionDto.setTransactionType("TOPUP");

        Transactions transaction;
        try {
            transaction = TopUpPackageTransactionMapper.toEntity(transactionDto, account);
        } catch (Exception e) {
            LOGGER.error("Error mapping DTO to entity: {}", e.getMessage());
            return CustomResponseEntity.error("Error mapping DTO to entity");
        }

        // Update or create AccountCDDetails
        try {
            AccountCDDetails updateLastDebitAmount = accountCDDetailsRepository.findByAccount_Id(account.getId());
            if (updateLastDebitAmount != null) {
                // Update existing AccountCDDetails
                updateLastDebitAmount.setDebit(billAmount);
                updateLastDebitAmount.setAccount(account);
                updateLastDebitAmount.setActualBalance(newBalance); // Set the current balance
                updateLastDebitAmount.setPreviousBalance(previousBalance); // Balance before deduction
                updateLastDebitAmount.setCredit(0.0);
            } else {
                // Create new AccountCDDetails if not found
                updateLastDebitAmount = new AccountCDDetails();
                updateLastDebitAmount.setDebit(billAmount);
                updateLastDebitAmount.setAccount(account);
                updateLastDebitAmount.setActualBalance(newBalance); // Set the current balance
                updateLastDebitAmount.setPreviousBalance(previousBalance); // Balance before deduction
                updateLastDebitAmount.setCredit(0.0);
            }
            accountCDDetailsRepository.save(updateLastDebitAmount);
        } catch (Exception e) {
            LOGGER.error("Error updating or creating AccountCDDetails: {}", e.getMessage());
            return CustomResponseEntity.error("Error updating or creating AccountCDDetails");
        }

        // Save transaction
        try {
            transactionsGenericDao.saveOrUpdate(transaction);
        } catch (Exception e) {
            LOGGER.error("Error saving transaction: {}", e.getMessage());
            return CustomResponseEntity.error("Error saving transaction");
        }

        // Update account balance in repository
        try {
            account.setAccountBalance(newBalance);
            accountRepository.save(account);
        } catch (Exception e) {
            LOGGER.error("Error updating account balance: {}", e.getMessage());
            return CustomResponseEntity.error("Error updating account balance");
        }

        // Combine additional details with existing response data
        @SuppressWarnings("unchecked")
        Map<String, Object> responseData = (Map<String, Object>) responseBody.getData();
        responseData.put("transactionId", transactionDto.getTransactionId());
        responseData.put("date", formatter.format(new Date()));
        responseData.put("mobileNumber", mobileNumber);

        // Return the updated response
        return new CustomResponseEntity<>(responseData, "Package Subscribed Succesfully");
    }



    @Override
    public CustomResponseEntity schdulePackageTransaction(Long packageId, String accountNumber, String mobileNumber,Long schduleId) {
        // Check if account exists
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            return CustomResponseEntity.error("Account doesn't exist");
        }

        // Build URI with query parameters
        URI uri;
        try {
            uri = UriComponentsBuilder.fromHttpUrl(BundleTransactionURL)
                    .queryParam("packageId", packageId)
                    .queryParam("mobileNumber", mobileNumber)
                    .build()
                    .toUri();
        } catch (Exception e) {
            LOGGER.error("Error building URI: {}", e.getMessage());
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
            scheduleMobileTopUpPayment.setStatus("In-Completed");
            scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            return CustomResponseEntity.error("Error building URI");
        }

        // Log the full request URL
        LOGGER.info("Request URL: {}", uri);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make HTTP GET request
        ResponseEntity<CustomResponseEntity> response;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CustomResponseEntity.class
            );
        } catch (Exception e) {
            LOGGER.error("Error making HTTP request: {}", e.getMessage());
            return CustomResponseEntity.error("Error making HTTP request");
        }

        // Process the response
        CustomResponseEntity<?> responseBody = response.getBody();
        if (responseBody == null) {
            LOGGER.error("Response body is null");
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
            scheduleMobileTopUpPayment.setStatus("In-Completed");
            scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            return CustomResponseEntity.error("Response body is null");
        }

        if (!responseBody.isSuccess()) {
            String errorMessage = responseBody.getMessage(); // Get error message from response body
            LOGGER.error("Error from response: {}", errorMessage);
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
            scheduleMobileTopUpPayment.setStatus("In-Completed");
            scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            return CustomResponseEntity.error(errorMessage);
        }

        // Extract amount from response data
        Double billAmount;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseBody.getData();
            Object billObject = data.get("package details");

            if (billObject instanceof Map) {
                Map<String,Object> topUpMap = (Map<String,Object>) billObject;
                Object priceObject = topUpMap.get("price");
                billAmount = ((Number) priceObject).doubleValue();
            } else {
                LOGGER.error("Invalid amount received in response: {}", billObject);
                ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
                scheduleMobileTopUpPayment.setStatus("In-Completed");
                scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
                return CustomResponseEntity.error("Invalid amount received in response");
            }
        } catch (Exception e) {
            LOGGER.error("Error processing response data: {}", e.getMessage());
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
            scheduleMobileTopUpPayment.setStatus("In-Completed");
            scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            return CustomResponseEntity.error("Error processing response data");
        }

        // Check account balance
        Double accountBalance = account.getAccountBalance();
        if (accountBalance == null) {
            accountBalance = 0.0;
        }
//        Double singleDayTopUpLimit = account.getSingleDayBillPayLimit();
        Double singleDayTopUpLimit = account.getSingleDayTopUpLimit();
        // 1. Fetch today's transactions and sum their debit amounts
//                        LocalDate today2 = LocalDate.now();
        Double totalDebitToday = calculateTodayTotalDebit(account.getId());

        // 2. Check if the new transaction amount plus today's total exceeds the limit
        if (totalDebitToday + billAmount > singleDayTopUpLimit) {
            LOGGER.warn("Daily transaction limit exceeded for account number: {}", account.getAccountNumber());
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
            scheduleMobileTopUpPayment.setStatus("In-Completed");
            scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            return new CustomResponseEntity<>(1002, "Daily transaction limit exceeded ....");
        }

        if (accountBalance < billAmount) {
            LOGGER.error("Insufficient balance. Current balance: {}, Required amount: {}", accountBalance, billAmount);
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
            scheduleMobileTopUpPayment.setStatus("In-Completed");
            scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            return CustomResponseEntity.error("Insufficient balance");
        }

        // Record the balance before the deduction
        Double previousBalance = accountBalance;

        // Perform the deduction
        Double newBalance = accountBalance - billAmount;

        // Create transaction DTO
        TopPackageTransactionDto transactionDto = new TopPackageTransactionDto();
        transactionDto.setAccountNumber(account.getAccountNumber());
        transactionDto.setDebitAmount(billAmount);
        transactionDto.setCreditAmount(0.0);
        transactionDto.setCurrentBalance(newBalance);
        transactionDto.setTransactionId(generateRandomReference());
        transactionDto.setTransactionDate(formatter.format(new Date()));
        transactionDto.setIbanCode(account.getIbanCode());
        transactionDto.setTransactionNarration("Top-up transaction");
        transactionDto.setTransactionType("TOPUP");

        Transactions transaction;
        try {
            transaction = TopUpPackageTransactionMapper.toEntity(transactionDto, account);
        } catch (Exception e) {
            LOGGER.error("Error mapping DTO to entity: {}", e.getMessage());
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
            scheduleMobileTopUpPayment.setStatus("In-Completed");
            scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            return CustomResponseEntity.error("Error mapping DTO to entity");
        }

        // Update or create AccountCDDetails
        try {
            AccountCDDetails updateLastDebitAmount = accountCDDetailsRepository.findByAccount_Id(account.getId());
            if (updateLastDebitAmount != null) {
                // Update existing AccountCDDetails
                updateLastDebitAmount.setDebit(billAmount);
                updateLastDebitAmount.setAccount(account);
                updateLastDebitAmount.setActualBalance(newBalance); // Set the current balance
                updateLastDebitAmount.setPreviousBalance(previousBalance); // Balance before deduction
                updateLastDebitAmount.setCredit(0.0);
            } else {
                // Create new AccountCDDetails if not found
                updateLastDebitAmount = new AccountCDDetails();
                updateLastDebitAmount.setDebit(billAmount);
                updateLastDebitAmount.setAccount(account);
                updateLastDebitAmount.setActualBalance(newBalance); // Set the current balance
                updateLastDebitAmount.setPreviousBalance(previousBalance); // Balance before deduction
                updateLastDebitAmount.setCredit(0.0);
            }
            accountCDDetailsRepository.save(updateLastDebitAmount);
        } catch (Exception e) {
            LOGGER.error("Error updating or creating AccountCDDetails: {}", e.getMessage());
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
            scheduleMobileTopUpPayment.setStatus("In-Completed");
            scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            return CustomResponseEntity.error("Error updating or creating AccountCDDetails");
        }

        // Save transaction
        try {
            transactionsGenericDao.saveOrUpdate(transaction);
        } catch (Exception e) {
            LOGGER.error("Error saving transaction: {}", e.getMessage());
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
            scheduleMobileTopUpPayment.setStatus("In-Completed");
            scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            return CustomResponseEntity.error("Error saving transaction");
        }

        // Update account balance in repository
        try {
            account.setAccountBalance(newBalance);
            accountRepository.save(account);
        } catch (Exception e) {
            LOGGER.error("Error updating account balance: {}", e.getMessage());
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
            scheduleMobileTopUpPayment.setStatus("In-Completed");
            scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            return CustomResponseEntity.error("Error updating account balance");
        }

        // Combine additional details with existing response data
        @SuppressWarnings("unchecked")
        Map<String, Object> responseData = (Map<String, Object>) responseBody.getData();
        responseData.put("transactionId", transactionDto.getTransactionId());
        responseData.put("date", formatter.format(new Date()));
        responseData.put("mobileNumber", mobileNumber);

        // Return the updated response
        ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = scheduleMobileTopUpPaymentRepository.findById(schduleId).orElse(null);
        scheduleMobileTopUpPayment.setStatus("Completed");
        scheduleMobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
        return null;
    }



    private List<Map<String, Object>> parseJsonArray(String jsonArrayString) {
        List<Map<String, Object>> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonArrayString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            list.add(jsonObject.toMap()); // Convert JSONObject to Map
        }

        return list;
    }
    public static String generateRandomReference() {
        int REFERENCE_LENGTH = 9;
        Random random = new Random();
        StringBuilder reference = new StringBuilder(REFERENCE_LENGTH);
        for (int i = 0; i < REFERENCE_LENGTH; i++) {
            reference.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        return reference.toString();
    }

    private Double calculateTodayTotalDebit(Long accountId) {
        LocalDate today = LocalDate.now();
        List<Transactions> todayTransactions = transactionRepository.findByAccount_IdAndTransactionTypeAndTransactionDateContaining(accountId,"TOPUP", today.toString());
        return todayTransactions.stream()
                .mapToDouble(Transactions::getDebitAmt)
                .sum();
    }

/*    private boolean isTransactionLimitExceeded(Double currentTotal, Double transactionAmount, Double limit) {
        return currentTotal + transactionAmount > limit;
    }*/


}

