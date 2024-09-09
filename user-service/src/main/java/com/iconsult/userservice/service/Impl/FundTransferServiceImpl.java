package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.constant.StatementType;
import com.iconsult.userservice.feignClient.BeneficiaryServiceClient;
import com.iconsult.userservice.model.dto.request.FundTransferDto;
import com.iconsult.userservice.model.dto.request.InterBankFundTransferDto;
import com.iconsult.userservice.model.dto.request.TransactionsDTO;
import com.iconsult.userservice.model.dto.response.FetchAccountDto;
import com.iconsult.userservice.model.dto.response.StatementDetailDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.AccountCDDetails;
import com.iconsult.userservice.model.entity.Bank;
import com.iconsult.userservice.model.entity.Transactions;
import com.iconsult.userservice.model.mapper.TransactionsMapper;
import com.iconsult.userservice.repository.AccountCDDetailsRepository;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.TransactionRepository;
import com.iconsult.userservice.service.FundTransferService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FundTransferServiceImpl implements FundTransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FundTransferServiceImpl.class);

    private final String getAccountTitleURL = "http://localhost:8081/transaction/fetchAccountTitle";

    private final String fundTransferURL = "http://localhost:8081/transaction/request";

    private final String interBankFundTransferURL = "http://192.168.0.63:8080/api/v1/1link/creditTransaction";

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private GenericDao<Bank> bankGenericDao;

    @Autowired
    private GenericDao<com.iconsult.userservice.model.entity.Transactions> transactionsGenericDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    GenericDao<Account> accountGenericDao;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    BeneficiaryServiceClient beneficiaryServiceClient;


    @Autowired
    private AccountCDDetailsRepository accountCDDetailsRepository;

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
                        AccountCDDetails receiverAccountCDDetails;
                        AccountCDDetails receiverAccountCDDetails2 = accountCDDetailsRepository.findByAccount_Id(receiverAccount.get().getId());
                        if (receiverAccountCDDetails2 != null) {
                            receiverAccountCDDetails = receiverAccountCDDetails2;
                            receiverAccountCDDetails.setActualBalance(receiverAccount.get().getAccountBalance() + cbsTransferDto.getTransferAmount());
                            receiverAccountCDDetails.setCredit(cbsTransferDto.getTransferAmount());
                            receiverAccountCDDetails.setPreviousBalance(receiverAccount.get().getAccountBalance());

                        } else {
                            receiverAccountCDDetails = new AccountCDDetails(receiverAccount.get(), receiverAccount.get().getAccountBalance() + cbsTransferDto.getTransferAmount(), receiverAccount.get().getAccountBalance(), cbsTransferDto.getTransferAmount(), 0.0);
                        }
                        AccountCDDetails senderAccountCDDetails;
                        AccountCDDetails senderAccountCDDetails2 = accountCDDetailsRepository.findByAccount_Id(senderAccount.get().getId());
                        if (senderAccountCDDetails2 != null) {
                            senderAccountCDDetails = senderAccountCDDetails2;
                            senderAccountCDDetails.setActualBalance(senderBalance);
                            senderAccountCDDetails.setDebit(cbsTransferDto.getTransferAmount());
                            senderAccountCDDetails.setPreviousBalance(senderAccount.get().getAccountBalance());

                        } else {
                            senderAccountCDDetails = new AccountCDDetails(senderAccount.get(), senderBalance, senderAccount.get().getAccountBalance(), 0.0, cbsTransferDto.getTransferAmount());
                        }
                        // Update sender's account details
                        senderAccount.get().setAccountBalance(senderBalance);

                        // Update receiver's account details
                        receiverAccount.get().setAccountBalance(receiverBalance);

                        // Save the updated accounts
                        accountGenericDao.saveOrUpdate(senderAccount.get());
                        accountGenericDao.saveOrUpdate(receiverAccount.get());

                        // 3. Log the transfer details (create and save Cbs_Transfer records for sender and receiver)
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String formattedDate = LocalDate.now().format(formatter);
//                        receiverAccount.get().getAccountCdDetails().setCredit(cbsTransferDto.getTransferAmount());
//                        receiverAccount.get().getAccountCdDetails().setPreviousBalance(receiverAccount.get().getAccountBalance());
//                        double totalBalanceReceiverAccount = receiverAccount.get().getAccountBalance() + cbsTransferDto.getTransferAmount();
//                        receiverAccount.get().getAccountCdDetails().setActualBalance(totalBalanceReceiverAccount);
//                        receiverAccount.get().setAccountBalance(totalBalanceReceiverAccount);
//                        senderAccount.get().getAccountCdDetails().setPreviousBalance(senderAccount.get().getAccountBalance());
//                        senderAccount.get().getAccountCdDetails().setDebit(cbsTransferDto.getTransferAmount());
//                        senderAccount.get().getAccountCdDetails().setActualBalance(senderBalance);
//                        senderAccount.get().setAccountBalance(senderBalance);
                        accountCDDetailsRepository.save(senderAccountCDDetails);
                        accountCDDetailsRepository.save(receiverAccountCDDetails);
                        senderAccount.get().setAccountCdDetails(senderAccountCDDetails);
                        receiverAccount.get().setAccountCdDetails(receiverAccountCDDetails);
                        accountRepository.save(senderAccount.get());
                        // Introduce an error to trigger a rollback
                        accountRepository.save(receiverAccount.get());

                        // Sender Transfer Log
                        Transactions fundsTransferSender = new Transactions();
                        fundsTransferSender.setAccount(senderAccount.get());
                        fundsTransferSender.setCurrentBalance(senderBalance);
                        fundsTransferSender.setDebitAmt(cbsTransferDto.getTransferAmount());
                        fundsTransferSender.setTransactionDate(formattedDate);
                        HashMap<String, String> map = (HashMap<String, String>) responseDto.getData();
                        fundsTransferSender.setTransactionId(map.get("paymentReference"));
                        fundsTransferSender.setCreditAmt(0.0);
                        fundsTransferSender.setSenderAccount(senderAccount.get().getAccountNumber());
                        fundsTransferSender.setReceiverAccount(receiverAccount.get().getAccountNumber());
                        fundsTransferSender.setCurrency(map.get("ccy"));
                        fundsTransferSender.setIbanCode(senderAccount.get().getIbanCode());
                        // Receiver Transfer Log
                        Transactions fundsTransferReceiver = new Transactions();
                        fundsTransferReceiver.setAccount(receiverAccount.get());
                        fundsTransferReceiver.setCurrentBalance(receiverBalance);
                        fundsTransferReceiver.setCreditAmt(cbsTransferDto.getTransferAmount());
                        fundsTransferReceiver.setTransactionDate(formattedDate);
                        fundsTransferReceiver.setTransactionId(map.get("paymentReference"));
                        fundsTransferReceiver.setDebitAmt(0.0);
                        fundsTransferReceiver.setReceiverAccount(receiverAccount.get().getAccountNumber());
                        fundsTransferReceiver.setSenderAccount(senderAccount.get().getAccountNumber());
                        fundsTransferReceiver.setCurrency(map.get("ccy"));
                        fundsTransferReceiver.setIbanCode(receiverAccount.get().getIbanCode());

                        // Save both transfer logs
                        transactionsGenericDao.saveOrUpdate(fundsTransferSender);
                        transactionsGenericDao.saveOrUpdate(fundsTransferReceiver);

                        beneficiaryServiceClient.addTransferAmountToBene(receiverAccount.get().getAccountNumber(), String.valueOf(transferAmount), receiverAccount.get().getCustomer().getId());


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

    @Override
    public CustomResponseEntity interBankFundTransfer(InterBankFundTransferDto fundTransferDto) {

        Account account = accountRepository.getAccountByAccountNumber(fundTransferDto.getFromAccountNumberOrIbanCode());
        if (account == null) {
            return new CustomResponseEntity("sender account not found within DiGi Bank!");
        }

        // 1% of transaction
        double transactionFee = fundTransferDto.getAmount() * 0.01;
        double totalAmount = fundTransferDto.getAmount() + transactionFee;

        if (totalAmount > account.getAccountBalance()) {
            Map<String, Object> map = new HashMap<>();
            map.put("accountNumber", account.getAccountNumber());
            map.put("currentBalance", account.getAccountBalance());
            return new CustomResponseEntity(map, "your account does not have a sufficient balance!");
        }


        String jpql = "SELECT c FROM Account c WHERE c.accountNumber = :accountNumber Or c.ibanCode = :accountNumber";
        Map<String, Object> params = new HashMap<>();
        params.put("accountNumber", fundTransferDto.getFromAccountNumberOrIbanCode());
        Optional<Account> senderAccount = Optional.ofNullable(accountGenericDao.findOneWithQuery(jpql, params));

        double senderBalance = senderAccount.get().getAccountBalance();
        senderBalance -= fundTransferDto.getAmount();


        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(interBankFundTransferURL)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Create HttpEntity with Cbs_TransferDto as the body and headers
            HttpEntity<InterBankFundTransferDto> entity = new HttpEntity<>(fundTransferDto, headers);

            // Make HTTP POST request
            ResponseEntity<CustomResponseEntity> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    CustomResponseEntity.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                CustomResponseEntity responseDto = response.getBody();

                if (responseDto != null && responseDto.isSuccess()) {

                    AccountCDDetails senderAccountCDDetails = accountCDDetailsRepository.findByAccount_Id(senderAccount.get().getId());

                    if (senderAccountCDDetails != null) {
                        senderAccountCDDetails.setActualBalance(senderBalance);
                        senderAccountCDDetails.setDebit(fundTransferDto.getAmount());
                        senderAccountCDDetails.setPreviousBalance(senderAccount.get().getAccountBalance());

                    } else {
                        senderAccountCDDetails = new AccountCDDetails(senderAccount.get(), senderBalance, senderAccount.get().getAccountBalance(), 0.0, fundTransferDto.getAmount());
                    }

                    accountCDDetailsRepository.save(senderAccountCDDetails);

                    Transactions fundsTransferSender = new Transactions();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String formattedDate = LocalDate.now().format(formatter);

                    fundsTransferSender.setAccount(account);
                    Map<String, Object> data = (Map<String, Object>) responseDto.getData();
                    Object billObject = data.get("transactionId");
                    fundsTransferSender.setTransactionId(String.valueOf(billObject));
                    fundsTransferSender.setTransactionNarration("IBFT");
                    fundsTransferSender.setCurrentBalance(account.getAccountBalance() - totalAmount);
                    fundsTransferSender.setDebitAmt(totalAmount);
                    fundsTransferSender.setTransactionDate(formattedDate);
                    fundsTransferSender.setCreditAmt(0.0);

                    fundsTransferSender.setBankCode(fundTransferDto.getBankCode());

                    transactionsGenericDao.saveOrUpdate(fundsTransferSender);

                    account.setAccountBalance(account.getAccountBalance() - totalAmount);
                    accountRepository.save(account);
                    return new CustomResponseEntity<>(responseDto, "Funds have been successfully transferred.");
                } else {
                    return new CustomResponseEntity("The recipient accountNumber or secretKey provided is incorrect. " +
                            "Please verify both and try again.");
                }
            } else {
                throw new RuntimeException("Failed to call API: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            LOGGER.error("Exception occurred: ", e);
            return CustomResponseEntity.error("Unable to process the request." +
                    " Please verify that the provided information is correct and try again.");
        }
    }

    @Override
    public CustomResponseEntity<Map<String, Object>> getTransactionsByAccountAndDateRange(
            String accountNumber, String startDate, String endDate) {

        LocalDate startDt = LocalDate.parse(startDate);
        LocalDate endDt = LocalDate.parse(endDate);

        if(startDt.isAfter(endDt)){
            return new CustomResponseEntity<>("Start date cannot be after end date.");
        }
        List<Transactions> transactions = transactionRepository.findTransactionsByAccountNumberAndDateRange(accountNumber, startDate, endDate);
        if(transactions.isEmpty()) {
           return new CustomResponseEntity<>("no transactions found against this account number within this date range");
        }
            List<TransactionsDTO> transactionDTOs = TransactionsMapper.toDTOList(transactions);

            StatementDetailDto statementDetailDto = new StatementDetailDto();
            Transactions tran = transactions.get(0);

            if (tran.getBankCode() != null) {
                statementDetailDto.setBankCode(tran.getBankCode());
            }
            statementDetailDto.setAccountNumber(tran.getAccount().getAccountNumber());
            statementDetailDto.setIBAN(tran.getIbanCode());
            statementDetailDto.setAccountTitle(tran.getAccount().getCustomer().getFirstName() + " " + tran.getAccount().getCustomer().getLastName());
            statementDetailDto.setRegisteredAddress(tran.getAccount().getCustomer().getRegisteredAddress());
            statementDetailDto.setRegisteredContact(tran.getAccount().getCustomer().getMobileNumber());
            Date accountOpenDate = tran.getAccount().getAccountOpenDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            statementDetailDto.setAccountOpenDate(formatter.format(accountOpenDate));
            statementDetailDto.setNatureOfAccount(tran.getNatureOfAccount());
            statementDetailDto.setCurrency(tran.getCurrency());

            CustomResponseEntity<List<TransactionsDTO>> response = new CustomResponseEntity<>();
            response.setData(transactionDTOs);

            Map<String, Object> map = new HashMap<>();
            map.put("AccountDetail", statementDetailDto);
            map.put("transactionList", response);


        if (!transactionDTOs.isEmpty()) {
            response.setSuccess(true);
            response.setMessage("Transactions fetched successfully.");
        } else {
            response.setMessage("No transactions found for the given criteria.");
        }
        return new CustomResponseEntity<>(map, "details");
    }

    @Override
    public CustomResponseEntity<Map<String, Object>> generateMiniStatement(String accountNumber) {

        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate today = LocalDate.now();
        LocalDate lastThreeMonths = today.minusMonths(1);

        String start = lastThreeMonths.format(DATE_FORMATTER);
        String end = today.format(DATE_FORMATTER);

        List<Transactions> transactions = transactionRepository.findTransactionsByAccountNumberAndDateRange(accountNumber, start, end);
        if(transactions.isEmpty()) {
            return new CustomResponseEntity<>("invalid account number");
        }
        List<TransactionsDTO> transactionDTOs = TransactionsMapper.toDTOList(transactions);

        StatementDetailDto statementDetailDto = new StatementDetailDto();
        Transactions tran = transactions.get(0);

        if (tran.getBankCode() != null) {
            statementDetailDto.setBankCode(tran.getBankCode());
        }
        statementDetailDto.setAccountNumber(tran.getAccount().getAccountNumber());
        statementDetailDto.setIBAN(tran.getIbanCode());
        statementDetailDto.setAccountTitle(tran.getAccount().getCustomer().getFirstName() + " " + tran.getAccount().getCustomer().getLastName());
        statementDetailDto.setRegisteredAddress(tran.getAccount().getCustomer().getRegisteredAddress());
        statementDetailDto.setRegisteredContact(tran.getAccount().getCustomer().getMobileNumber());
        Date accountOpenDate = tran.getAccount().getAccountOpenDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        statementDetailDto.setAccountOpenDate(formatter.format(accountOpenDate));
        statementDetailDto.setNatureOfAccount(tran.getNatureOfAccount());
        statementDetailDto.setCurrency(tran.getCurrency());

        CustomResponseEntity<List<TransactionsDTO>> response = new CustomResponseEntity<>();
        response.setData(transactionDTOs);

        Map<String, Object> map = new HashMap<>();
        map.put("AccountDetail", statementDetailDto);
        map.put("transactionList", response);


        if (!transactionDTOs.isEmpty()) {
            response.setSuccess(true);
            response.setMessage("Transactions fetched successfully.");
        } else {
            response.setMessage("No transactions found for the given criteria.");
        }
        return new CustomResponseEntity<>(map, "details");
    }

    @Override
    public CustomResponseEntity<Map<String, Object>> generateStatement(String accountNumber, String startDate, String endDate, String statementType) {

        StatementType statementTypeParam;
        try {
            statementTypeParam = StatementType.valueOf(statementType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return new CustomResponseEntity<>("Invalid Request Param for statement type!");
        }

        CustomResponseEntity<Map<String, Object>> transactionResponse;

        if (statementTypeParam == StatementType.MINI) {
            transactionResponse = generateMiniStatement(accountNumber);

            if (transactionResponse.getData() != null && !transactionResponse.getData().isEmpty()) {
                transactionResponse.setSuccess(true);
            }
            return transactionResponse;
        } else if (statementTypeParam == StatementType.DATE_RANGE) {
            transactionResponse = getTransactionsByAccountAndDateRange(accountNumber, startDate, endDate);
            if (transactionResponse.getData() != null && !transactionResponse.getData().isEmpty()) {
                transactionResponse.setSuccess(true);
            }
            return transactionResponse;

        } else {
            return new CustomResponseEntity<>("Please enter valid statement type param");
        }

    }


}
