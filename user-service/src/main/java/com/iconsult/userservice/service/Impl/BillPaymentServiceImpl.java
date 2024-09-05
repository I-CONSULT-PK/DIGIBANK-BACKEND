package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.model.dto.request.BillPaymentDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.Transactions;
import com.iconsult.userservice.repository.AccountRepository;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

@Service
public class BillPaymentServiceImpl implements BillPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillPaymentServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private GenericDao<Transactions> transactionsGenericDao;

    private final String URL = "http://192.168.0.152:8078/v1/billpayment/getBillDetails";

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

                    // Convert amountObject to a Double
                    Double amount = convertToDouble(amountObject);
                    Double afterDueDateAmount = convertToDouble(afterDueDateAmountObject);

                    // Check due date and select the amount to deduct
                    if (dueDateString != null) {
                        LocalDate dueDate = LocalDate.parse(dueDateString); // Parse dueDateString to LocalDate
                        LocalDate today = LocalDate.now();

                        if (dueDate.isBefore(today)) {
                            // Due date has passed, use afterDueDateAmount
                            amount = afterDueDateAmount != null ? afterDueDateAmount : amount;
                        }
                    }

                    // Fetch account details
                    Account account = accountRepository.findByAccountNumber(billPaymentDto.getAccountNumber());

                    if (account != null) {
                        Double accountBalance = account.getAccountBalance();

                        // Deduct amount from account balance
                        accountBalance -= amount;

                        // Update account balance
                        account.setAccountBalance(accountBalance);

                        // Create and set up the transaction
                        Transactions updateTransactionForBill = new Transactions();
                        updateTransactionForBill.setAccount(account); // Set the account reference
                        updateTransactionForBill.setDebitAmt(amount);
                        updateTransactionForBill.setCreditAmt(0.0);
                        updateTransactionForBill.setCurrentBalance(accountBalance);
                        updateTransactionForBill.setNatureOfAccount(account.getAccountType());
                        // Save the updated account and transaction back to the database
                        accountRepository.save(account);
                        transactionsGenericDao.saveOrUpdate(updateTransactionForBill);

                        LOGGER.info("Account balance and transaction updated successfully");
                    } else {
                        LOGGER.warn("Account not found for account number: {}", billPaymentDto.getAccountNumber());
                        throw new RuntimeException("Account not found");
                    }

                    // Return the successful response body
                    return new CustomResponseEntity<>(data, "Utility details processed successfully");
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

    private Double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }


}