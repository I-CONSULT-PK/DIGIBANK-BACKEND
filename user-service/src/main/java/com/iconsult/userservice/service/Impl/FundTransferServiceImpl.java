package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.model.dto.request.FundTransferDto;
import com.iconsult.userservice.model.dto.response.FetchAccountDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.Bank;
import com.iconsult.userservice.model.entity.Transactions;
import com.iconsult.userservice.service.FundTransferService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FundTransferServiceImpl implements FundTransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FundTransferServiceImpl.class);

    private final String getAccountTitleURL = "http://localhost:8081/transaction/fetchAccountTitle";

    private final String fundTransferURL = "http://localhost:8081/transaction/request";

    @Autowired
    private GenericDao<Bank> bankGenericDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    GenericDao<Account> accountGenericDao;

    @Autowired
    GenericDao<Transactions> transactionsGenericDao;

    @Override
    public CustomResponseEntity getAllBanks() {
        LOGGER.info("GetAllBanks Request Received...");

        try {
            String jpql = "SELECT c FROM Bank c WHERE c.isActive = :isActive";
            Map<String, Object> params = new HashMap<>();
            params.put("isActive", true);

            List<Bank> bankList = bankGenericDao.findWithQuery(jpql, params);

            if (bankList.isEmpty()) {
                LOGGER.info("No Banks Exist!");
                return CustomResponseEntity.error("No Banks Exist!");
            }

            return new CustomResponseEntity<>(bankList, "Bank List");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return CustomResponseEntity.error("Unable to Process!");
        }
    }

    @Override
    public CustomResponseEntity getAccountTitle(String senderAccountNumber) {
        try {
            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(fundTransferURL)
                    .queryParam("accountNumber", senderAccountNumber)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri);

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
                CustomResponseEntity<FetchAccountDto> responseDto = response.getBody();
                if (responseDto != null) {
                    // Print or log responseDto to verify its content
                    LOGGER.info("Received CustomerDto: " + responseDto.getMessage());
                    if (!responseDto.isSuccess()) return responseDto;

                    return responseDto;
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

    public CustomResponseEntity fundTransfer(FundTransferDto cbsTransferDto) {
        try {
            // Build the URI for the POST request
            URI uri = UriComponentsBuilder.fromHttpUrl(fundTransferURL)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Create HttpEntity with Cbs_TransferDto as the body and headers
            HttpEntity<FundTransferDto> entity = new HttpEntity<>(cbsTransferDto, headers);

            // Make HTTP POST request
            ResponseEntity<CustomResponseEntity> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    CustomResponseEntity.class
            );

            // Handle response
            if (response.getStatusCode() == HttpStatus.OK) {
                CustomResponseEntity responseDto = response.getBody();
                if (responseDto != null && responseDto.isSuccess()) {
                    // Process the success response

                    // 1. Retrieve sender and receiver accounts locally using the same logic from the transaction method
                    String jpql = "SELECT c FROM Account c WHERE c.accountNumber = :accountNumber Or c.ibanCode = :accountNumber";
                    Map<String, Object> params = new HashMap<>();
                    params.put("accountNumber", cbsTransferDto.getSenderAccountNumber());

                    Optional<Account> senderAccount = Optional.ofNullable(accountGenericDao.findOneWithQuery(jpql, params));
                    params.put("accountNumber", cbsTransferDto.getReceiverAccountNumber());
                    Optional<Account> receiverAccount = Optional.ofNullable(accountGenericDao.findOneWithQuery(jpql, params));

                    if (senderAccount.isPresent() && receiverAccount.isPresent()) {
                        // 2. Apply credit and debit logic
                        double senderBalance = senderAccount.get().getAccountBalance();
                        double receiverBalance = receiverAccount.get().getAccountBalance();
                        double transferAmount = cbsTransferDto.getTransferAmount();

                        senderBalance -= transferAmount;
                        receiverBalance += transferAmount;

                        // Update sender's account details
                        senderAccount.get().setAccountBalance(senderBalance);

                        // Update receiver's account details
                        receiverAccount.get().setAccountBalance(receiverBalance);

                        // Save the updated accounts
                        accountGenericDao.saveOrUpdate(senderAccount.get());
                        accountGenericDao.saveOrUpdate(receiverAccount.get());

                        // 3. Log the transfer details (create and save Cbs_Transfer records for sender and receiver)
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDateTime = LocalDateTime.now().format(formatter);

                        // Sender Transfer Log
                        Transactions fundsTransferSender = new Transactions();
                        fundsTransferSender.setAccount(senderAccount.get());
                        fundsTransferSender.setCurrentBalance(senderBalance);
                        fundsTransferSender.setDebitAmt(cbsTransferDto.getTransferAmount());
                        fundsTransferSender.setTransactionDate(String.valueOf(new Date()));
                        fundsTransferSender.setCreditAmt(0.0);
                        // Receiver Transfer Log
                        Transactions fundsTransferReceiver = new Transactions();
                        fundsTransferReceiver.setAccount(receiverAccount.get());
                        fundsTransferReceiver.setCurrentBalance(receiverBalance);
                        fundsTransferReceiver.setCreditAmt(cbsTransferDto.getTransferAmount());
                        fundsTransferReceiver.setTransactionDate(String.valueOf(new Date()));
                        fundsTransferReceiver.setDebitAmt(0.0);

                        // Save both transfer logs
                        transactionsGenericDao.saveOrUpdate(fundsTransferSender);
                        transactionsGenericDao.saveOrUpdate(fundsTransferReceiver);

                        // Return success message
                        return new CustomResponseEntity<>(responseDto, "Transaction successful.");
                    } else {
                        // Handle missing accounts locally
                        return CustomResponseEntity.error("Local accounts not found.");
                    }
                } else {
                    // CBS service indicated failure
                    return CustomResponseEntity.error(responseDto != null ? responseDto.getMessage() : "Unable to Process!");
                }
            } else {
                // Non-200 status response
                LOGGER.error("Unexpected response status: " + response.getStatusCode());
                return CustomResponseEntity.error("Unable to Process!");
            }
        } catch (Exception e) {
            // Handle exceptions
            LOGGER.error("Exception occurred: ", e);
            return CustomResponseEntity.error("Unable to Process!");
        }
    }




}
