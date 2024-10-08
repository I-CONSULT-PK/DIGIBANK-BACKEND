package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.constant.StatementType;
import com.iconsult.userservice.custome.Regex;
import com.iconsult.userservice.dto.UserActivityRequest;
import com.iconsult.userservice.feignClient.BeneficiaryServiceClient;
import com.iconsult.userservice.model.dto.request.FundTransferDto;
import com.iconsult.userservice.model.dto.request.InterBankFundTransferDto;
import com.iconsult.userservice.model.dto.request.ScheduleFundTransferDto;
import com.iconsult.userservice.model.dto.request.TransactionsDTO;
import com.iconsult.userservice.model.dto.response.FetchAccountDto;
import com.iconsult.userservice.model.dto.response.StatementDetailDto;
import com.iconsult.userservice.model.entity.*;
import com.iconsult.userservice.model.mapper.TransactionsMapper;
import com.iconsult.userservice.repository.AccountCDDetailsRepository;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.repository.ScheduledTransactionsRepository;
import com.iconsult.userservice.repository.TransactionRepository;
import com.iconsult.userservice.service.FundTransferService;
import com.iconsult.userservice.service.NotificationService;
import com.iconsult.userservice.service.UserActivityService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FundTransferServiceImpl implements FundTransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FundTransferServiceImpl.class);

    private final String getAccountTitleURL = "http://localhost:8081/transaction/fetchAccountTitle";

    private final String fundTransferURL = "http://localhost:8081/transaction/request";

    private final String interBankFundTransferURL = "http://localhost:8084/api/v1/1link/creditTransaction";
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    Regex regex;
    @Autowired
    private GenericDao<Bank> bankGenericDao;

    @Autowired
    private GenericDao<com.iconsult.userservice.model.entity.Transactions> transactionsGenericDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    GenericDao<Account> accountGenericDao;

    @Autowired
    UserActivityService userActivityService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeneficiaryServiceClient beneficiaryServiceClient;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AccountCDDetailsRepository accountCDDetailsRepository;

//    @Autowired
//    private Scheduler scheduler;
    @Autowired
    private ScheduledTransactionsRepository scheduledTransactionsRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
    private Double calculateTotalDailyAmount(String account) {

        LocalDate today = LocalDate.now();

        // Start and end of the day
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999_999_999);

        // Formatters for converting LocalDateTime to String
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Convert to String
        String startOfDayStr = startOfDay.format(formatter);
        String endOfDayStr = endOfDay.format(formatter);

        List<Transactions> transactions = transactionRepository.findTransactionsByAccountNumberAndDateRange(
                account, startOfDayStr, endOfDayStr);

        return transactions.stream()
                .mapToDouble(Transactions::getDebitAmt)
                .sum();
    }

    public boolean isTransactionAllowed(String account, Double transactionAmount, Double singleDayLimit) {
        Double totalTransactionsForToday = calculateTotalDailyAmount(account);

        if (totalTransactionsForToday + transactionAmount <= singleDayLimit) {
            return true;
        }
        return false;
    }
    public CustomResponseEntity fundTransfer(FundTransferDto cbsTransferDto) {
            CustomResponseEntity sender =  regex.checkAccountNumberFormat(cbsTransferDto.getSenderAccountNumber());
            CustomResponseEntity res =  regex.checkAccountNumberFormat(cbsTransferDto.getReceiverAccountNumber());
            if(!sender.isSuccess()){
                return sender;
            }
            if(!res.isSuccess()){
                return res;
            }

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
                    if (senderAccount.isEmpty()){
                        return CustomResponseEntity.error("Invalid Account Number");
                    } else if (receiverAccount.isEmpty()) {
                        return CustomResponseEntity.error("Invalid Receiver account");
                    }
                    if (senderAccount.isPresent() && receiverAccount.isPresent()) {
                        if(senderAccount.get().getTransactionLimit() < cbsTransferDto.getTransferAmount()) {
                            return CustomResponseEntity.error("Account limit is lower than the transfer money");
                        }
//                        if (isTransactionAllowed(senderAccount.get().getAccountNumber(),cbsTransferDto.getTransferAmount(),senderAccount.get().getSingleDayLimit()) == false){
//                            return CustomResponseEntity.error("Single Day Account limit is lower than the transfer money");
//                        }
                        if(senderAccount.get().getCustomer().getId().equals(receiverAccount.get().getCustomer().getId())){
                            if (isTransactionAllowed(senderAccount.get().getAccountNumber(),cbsTransferDto.getTransferAmount(),senderAccount.get().getSingleDayOwnLimit()) == false){
                                return CustomResponseEntity.error("Single Day Account limit is lower than the transfer money");
                            }
                        }
                        if(cbsTransferDto.getSingleDayQRLimit().equals("qrpay")){
                            if (isTransactionAllowed(senderAccount.get().getAccountNumber(),cbsTransferDto.getTransferAmount(),senderAccount.get().getSingleDayQRLimit()) == false){
                                return CustomResponseEntity.error("Single Day Account limit is lower than the transfer money");
                            }
                        }


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
                        fundsTransferSender.setCustomer(senderAccount.get().getCustomer());
                        fundsTransferSender.setSenderAccount(senderAccount.get().getAccountNumber());
                        fundsTransferSender.setReceiverAccount(receiverAccount.get().getAccountNumber());
                        fundsTransferSender.setCurrency(map.get("ccy"));
                        fundsTransferSender.setIbanCode(senderAccount.get().getIbanCode());
                        fundsTransferSender.setStatus("COMPLETED");
                        // Receiver Transfer Log
                        Transactions fundsTransferReceiver = new Transactions();
                        fundsTransferReceiver.setAccount(receiverAccount.get());
                        fundsTransferReceiver.setCurrentBalance(receiverBalance);
                        fundsTransferReceiver.setCreditAmt(cbsTransferDto.getTransferAmount());
                        fundsTransferReceiver.setTransactionDate(formattedDate);
                        fundsTransferReceiver.setStatus("COMPLETED");

                        fundsTransferReceiver.setTransactionId(
                                map.get("paymentReference") != null ? map.get("paymentReference") : null );

//                        fundsTransferReceiver.setTransactionId(map.get("paymentReference"));
                        fundsTransferReceiver.setDebitAmt(0.0);
                        fundsTransferReceiver.setCustomer(receiverAccount.get().getCustomer());
                        fundsTransferReceiver.setReceiverAccount(receiverAccount.get().getAccountNumber());
                        fundsTransferReceiver.setSenderAccount(senderAccount.get().getAccountNumber());
                        fundsTransferReceiver.setCurrency(map.get("ccy"));
                        fundsTransferReceiver.setIbanCode(receiverAccount.get().getIbanCode());

                        // Save both transfer logs
                        transactionsGenericDao.saveOrUpdate(fundsTransferSender);
                        transactionsGenericDao.saveOrUpdate(fundsTransferReceiver);
                        beneficiaryServiceClient.addTransferAmountToBene(receiverAccount.get().getAccountNumber(), String.valueOf(transferAmount), receiverAccount.get().getCustomer().getId());
                        UserActivityRequest userActivity = new UserActivityRequest();
                        userActivity.setActivityDate(LocalDateTime.now());
                        userActivity.setCustomerId(senderAccount.get().getCustomer());
                        userActivity.setUserActivity("Transferred an amount of Rs. "+cbsTransferDto.getTransferAmount()
                            +" from Account No: "+cbsTransferDto.getSenderAccountNumber()
                            +" To Account No :"+receiverAccount.get().getAccountNumber());
                        userActivity.setPkr(cbsTransferDto.getTransferAmount());
                        userActivityService.saveUserActivity(userActivity);

                        NotificationEvent notificationEvent = new NotificationEvent();
                        notificationEvent.setNotificationType("Funds Transfer");
                        notificationEvent.setMessage("An amount of "+cbsTransferDto.getTransferAmount()+
                                " has been successfully transferred from your account '"+
                                cbsTransferDto.getSenderAccountNumber()+"' to account '" +
                                cbsTransferDto.getReceiverAccountNumber()+"'.");
                        notificationEvent.setRecipientId(senderAccount.get().getCustomer().getId());
                        notificationEvent.setChannel("EMAIL");
                        notificationEvent.setTimeStamp(new Timestamp(System.currentTimeMillis()));
                        notificationEvent.setEmail(senderAccount.get().getCustomer().getEmail());

                        notificationService.sendNotification(notificationEvent);

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

        String jpql = "SELECT c FROM Account c WHERE c.accountNumber = :accountNumber Or c.ibanCode = :accountNumber";
        Map<String, Object> params = new HashMap<>();
        params.put("accountNumber", fundTransferDto.getSenderAccountNumber());

        Optional<Account> account  = Optional.ofNullable(accountGenericDao.findOneWithQuery(jpql, params));
//        Account account = accountRepository.getAccountByAccountNumber(fundTransferDto.getFromAccountNumber());
        if (!account.isPresent()) {
            return CustomResponseEntity.error("sender account not found within DiGi Bank!");
        }


        // 1% of transaction
        double transactionFee = fundTransferDto.getAmount() * 0.01;
        double totalAmount = fundTransferDto.getAmount() + transactionFee;
        if(account.get().getTransactionLimit() < totalAmount) {
            return CustomResponseEntity.error("Account limit is lower than the transfer money");
        }

        if (totalAmount > account.get().getAccountBalance()) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("accountNumber", account.get().getAccountNumber());
//            map.put("currentBalance", account.get().getAccountBalance());
            return CustomResponseEntity.error("Insufficient balance!");
        }
//        if (isTransactionAllowed(account.get().getAccountNumber(),totalAmount,account.get().getSingleDaySendToOtherBankLimit()) == false){
//            return CustomResponseEntity.error("Single Day Account limit is lower than the transfer money");
//        }
//        if (isTransactionAllowed(account.get().getAccountNumber(),totalAmount,account.get().getSingleDayLimit()) == false){
//            return CustomResponseEntity.error("Single Day Account limit is lower than the transfer money");
//        }


//        String jpql = "SELECT c FROM Account c WHERE c.accountNumber = :accountNumber Or c.ibanCode = :accountNumber";
//        Map<String, Object> params = new HashMap<>();
//        params.put("accountNumber", fundTransferDto.getFromAccountNumber());
//        Optional<Account> senderAccount = Optional.ofNullable(accountGenericDao.findOneWithQuery(jpql, params));

        double senderBalance = account.get().getAccountBalance();
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

                    AccountCDDetails senderAccountCDDetails = accountCDDetailsRepository.findByAccount_Id(account.get().getId());

                    if (senderAccountCDDetails != null) {
                        senderAccountCDDetails.setActualBalance(senderBalance-transactionFee);
                        senderAccountCDDetails.setDebit(fundTransferDto.getAmount()+transactionFee);
                        senderAccountCDDetails.setPreviousBalance(account.get().getAccountBalance());

                    } else {
                        senderAccountCDDetails = new AccountCDDetails(account.get(), senderBalance, account.get().getAccountBalance(), 0.0, fundTransferDto.getAmount());
                    }

                    accountCDDetailsRepository.save(senderAccountCDDetails);

                    Transactions fundsTransferSender = new Transactions();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String formattedDate = LocalDate.now().format(formatter);

                    fundsTransferSender.setAccount(account.get());
                    Map<String, Object> data = (Map<String, Object>) responseDto.getData();
                    Object billObject = data.get("transactionId");
                    fundsTransferSender.setTransactionId(String.valueOf(billObject));
                    fundsTransferSender.setTransactionNarration("IBFT");
                    fundsTransferSender.setCurrentBalance(account.get().getAccountBalance() - totalAmount);
                    fundsTransferSender.setDebitAmt(totalAmount);
                    fundsTransferSender.setTransactionDate(formattedDate);
                    fundsTransferSender.setCustomer(account.get().getCustomer());
                    fundsTransferSender.setCreditAmt(0.0);
                    fundsTransferSender.setStatus("COMPLETED");

                    fundsTransferSender.setBankCode(fundTransferDto.getBankCode());

                    transactionsGenericDao.saveOrUpdate(fundsTransferSender);

                    account.get().setAccountBalance(account.get().getAccountBalance() - totalAmount);
                    accountRepository.save(account.get());
                    UserActivityRequest userActivity = new UserActivityRequest();
                    userActivity.setActivityDate(LocalDateTime.now());
                    userActivity.setCustomerId(senderAccountCDDetails.getAccount().getCustomer());
                    userActivity.setUserActivity("From : "+fundTransferDto.getSenderAccountNumber()+" To : "+fundTransferDto.getReceiverAccountNumber()
                            +" Amount : "+fundTransferDto.getAmount());
                    userActivity.setPkr(fundTransferDto.getAmount());
                    userActivityService.saveUserActivity(userActivity);

                    NotificationEvent notificationEvent = new NotificationEvent();
                    notificationEvent.setNotificationType("Funds Transfer");
                    notificationEvent.setMessage("An amount of "+fundTransferDto.getAmount()+
                            " has been successfully transferred from your account '"+
                            fundTransferDto.getSenderAccountNumber()+"' to account '" +
                            fundTransferDto.getReceiverAccountNumber()+"'.");
                    notificationEvent.setRecipientId(account.get().getCustomer().getId());
                    notificationEvent.setChannel("EMAIL");
                    notificationEvent.setTimeStamp(new Timestamp(System.currentTimeMillis()));
                    notificationEvent.setEmail(account.get().getCustomer().getEmail());

                    notificationService.sendNotification(notificationEvent);

                    return new CustomResponseEntity<>(responseDto, "Funds have been successfully transferred.");
                } else {
                    return CustomResponseEntity.error("The Bank Code or Recipient Account Number is invalid. " +
                            "Please verify and try again.");
                }
            } else {
                return CustomResponseEntity.error("Failed to call API: " + response.getStatusCode());
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
        Account account = accountRepository.findByAccountNumber(accountNumber);

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
            UserActivityRequest userActivity = new UserActivityRequest();
            userActivity.setActivityDate(LocalDateTime.now());
            userActivity.setCustomerId(account.getCustomer());
      //      userActivity.setCustomerId(String.valueOf(account.getCustomer().getId()));
            userActivity.setUserActivity("User Generated The Account Statements");
            userActivity.setPkr(0.0);
            userActivityService.saveUserActivity(userActivity);

        } else {
            response.setMessage("No transactions found for the given criteria.");
        }
        return new CustomResponseEntity<>(map, "details");
    }

    @Override
    public CustomResponseEntity<Map<String, Object>> generateMiniStatement(String accountNumber) {

        LocalDate today = LocalDate.now();
        LocalDate lastThreeMonths = today.minusMonths(1);

        String start = lastThreeMonths.format(DATE_FORMATTER);
        String end = today.format(DATE_FORMATTER);
        Account account = accountRepository.findByAccountNumber(accountNumber);

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
            UserActivityRequest userActivity = new UserActivityRequest();
            userActivity.setActivityDate(LocalDateTime.now());
            userActivity.setCustomerId(account.getCustomer());
//            userActivity.setCustomerId(String.valueOf(account.getCustomer());
            //userActivity.setCustomerId(String.valueOf(account.getCustomer().getId()));
            userActivity.setUserActivity("User Generated The Account Statements");
            userActivity.setPkr(0.0);
            userActivityService.saveUserActivity(userActivity);
        } else {
            response.setMessage("No transactions found for the given criteria.");
        }
        return new CustomResponseEntity<>(map, "details");
    }

    @Override
    public CustomResponseEntity generateStatement(String accountNumber, String startDate, String endDate, String statementType) {

        if (!regex.isValidDate(startDate)) {
            return CustomResponseEntity.error("Start date must be in the format YYYY-MM-DD");
        }

        // Manual validation for endDate
        if (!regex.isValidDate(endDate)) {
            return CustomResponseEntity.error("End date must be in the format YYYY-MM-DD");
        }
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

    // Method to manually check if a date matches the format "yyyy-MM-dd"


    @Override
    public CustomResponseEntity setOneDayLimit(String accountNumber,Long customerId, Double ondDayLimit) {
        CustomResponseEntity accountFormat = regex.checkAccountNumberFormat(accountNumber);
        if (!accountFormat.isSuccess()){
            return accountFormat;
        }
        if(ondDayLimit > 1000000){
            return CustomResponseEntity.error("The one-day limit cannot exceed one million");
        }
        if (ondDayLimit<1){
            return CustomResponseEntity.error("The one-day limit can't be  less than 1");
        }
        Customer customer = customerRepository.findById(customerId).orElse(null);
        String jpql = "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber and a.customer= :customer";
        Map<String, Object> params = new HashMap<>();
        params.put("accountNumber", accountNumber);
        params.put("customer", customer);
        Account account = accountGenericDao.findOneWithQuery(jpql, params);
        if(Objects.isNull(account)){
            return new CustomResponseEntity<>("Account Does Not Exist");

        }
        account.setSingleDayLimit(ondDayLimit);
        accountGenericDao.saveOrUpdate(account);
        CustomResponseEntity customResponse = new CustomResponseEntity<>( "One Day Transaction limit set to : " + ondDayLimit);
        return customResponse;

    }

    @Override
    public CustomResponseEntity scheduleFundTransfer(ScheduleFundTransferDto fundTransferDto) throws SchedulerException {
//        try {
//            CustomResponseEntity sender = regex.checkAccountNumberFormat(fundTransferDto.getSenderAccountNumber());
//            CustomResponseEntity res = regex.checkAccountNumberFormat(fundTransferDto.getReceiverAccountNumber());
//            if (!sender.isSuccess()) {
//                return sender;
//            }
//            if (!res.isSuccess()) {
//                return res;
//            }
//            // 1. Retrieve sender and receiver accounts locally using the same logic from the transaction method
//            String jpql = "SELECT c FROM Account c WHERE c.accountNumber = :accountNumber Or c.ibanCode = :accountNumber";
//            Map<String, Object> params = new HashMap<>();
//            params.put("accountNumber", fundTransferDto.getSenderAccountNumber());
//
//            Optional<Account> senderAccount = Optional.ofNullable(accountGenericDao.findOneWithQuery(jpql, params));
//            params.put("accountNumber", fundTransferDto.getReceiverAccountNumber());
//            Optional<Account> receiverAccount = Optional.ofNullable(accountGenericDao.findOneWithQuery(jpql, params));
//            if (senderAccount.isEmpty()) {
//                LOGGER.info("Invalid Account Number");
//                return CustomResponseEntity.error("Invalid Account Number");
//            } else if (receiverAccount.isEmpty()) {
//                LOGGER.info("Invalid Account Number");
//                return CustomResponseEntity.error("Invalid Receiver account");
//            }
//            if (senderAccount.isPresent() && receiverAccount.isPresent()) {
//                if (senderAccount.get().getTransactionLimit() < fundTransferDto.getTransferAmount()) {
//                    LOGGER.info("Account limit is lower than the transfer money");
//                    return CustomResponseEntity.error("Account limit is lower than the transfer money");
//                }
//                if (isTransactionAllowed(senderAccount.get().getAccountNumber(), fundTransferDto.getTransferAmount(), senderAccount.get().getSingleDayLimit()) == false) {
//                    LOGGER.info("Single Day Account limit is lower than the transfer money");
//                    return CustomResponseEntity.error("Single Day Account limit is lower than the transfer money");
//                }
////                double senderBalance = senderAccount.get().getAccountBalance();
////                senderBalance -= fundTransferDto.getTransferAmount();
//                ScheduledTransactions fundsTransferSender = new ScheduledTransactions();
//                fundsTransferSender.setAccount(senderAccount.get());
//                fundsTransferSender.setCurrentBalance(senderAccount.get().getAccountBalance());
//                fundsTransferSender.setDebitAmt(fundTransferDto.getTransferAmount());
//                fundsTransferSender.setTransactionDate(fundTransferDto.getLocalDate().toString());
////                HashMap<String, String> map = (HashMap<String, String>) responseDto.getData();
////                fundsTransferSender.setTransactionId(map.get("paymentReference"));
//                fundsTransferSender.setCreditAmt(0.0);
//                fundsTransferSender.setSenderAccount(senderAccount.get().getAccountNumber());
//                fundsTransferSender.setReceiverAccount(receiverAccount.get().getAccountNumber());
////                fundsTransferSender.setCurrency(map.get("ccy"));
//                fundsTransferSender.setIbanCode(senderAccount.get().getIbanCode());
//                fundsTransferSender.setTransactionNarration("Scheduled Payment");
//                fundsTransferSender.setStatus("In Progress");
//                ScheduledTransactions scheduledTransactions = scheduledTransactionsRepository.save(fundsTransferSender);
//
//
//
//                JobDataMap jobDataMap = new JobDataMap();
//                jobDataMap.put("senderAccountNumber", fundTransferDto.getSenderAccountNumber());
//                jobDataMap.put("receiverAccountNumber", fundTransferDto.getReceiverAccountNumber());
//                jobDataMap.put("transferAmount", fundTransferDto.getTransferAmount());
//                jobDataMap.put("bankName", fundTransferDto.getBankName());
//                jobDataMap.put("purpose", fundTransferDto.getPurpose());
//                jobDataMap.put("date", fundTransferDto.getLocalDate());
//                jobDataMap.put("scheduleId", scheduledTransactions.getId());
//
//                JobDetail job = newJob(MyJobExecutor.class)
//                        .withIdentity("jobIdentity-"+scheduledTransactions.getId())
//                        .usingJobData(jobDataMap)
//                        .build();
//                Trigger trigger = newTrigger()
//                        .withIdentity("triggerIdentity-"+scheduledTransactions.getId())
//                        .startAt(fundTransferDto.getLocalDate())
//                        .withSchedule(simpleSchedule()
//                                .withIntervalInSeconds(60)
//                                .withRepeatCount(1)
//                                .withMisfireHandlingInstructionNextWithRemainingCount()
//                                )
//                        .build();
//
//
//                scheduler.scheduleJob(job, trigger);
//                LOGGER.info("Payment Scheduled");
//            }
//
//            return new CustomResponseEntity("Payment Schedule Successfully");
//        } catch (SchedulerException e) {
//            LOGGER.error(e.getMessage());
//            return CustomResponseEntity.error(e.getMessage());
//        }
//        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
//        String senderAccountNumber = (String) jobDataMap.get("senderAccountNumber");
//        String receiverAccountNumber = (String) jobDataMap.get("receiverAccountNumber");
//        Double transferAmount = (Double) jobDataMap.get("transferAmount");
//        String bankName = (String) jobDataMap.get("bankName");
//        String purpose = (String) jobDataMap.get("purpose");
//        Date date = (Date) jobDataMap.get("date");
//        Long scheduleId = (Long) jobDataMap.get("scheduleId");
//            String jobId = (String) jobDataMap.get("jobID");
//            LOGGER.info("Job Started-"+jobId+" at:"+new Date());

        String senderAccountNumber = fundTransferDto.getSenderAccountNumber();
        String receiverAccountNumber = fundTransferDto.getReceiverAccountNumber();
        Double transferAmount =fundTransferDto.getTransferAmount();
        String bankName =fundTransferDto.getBankName();
        String purpose = fundTransferDto.getPurpose();
        LocalDateTime date = fundTransferDto.getLocalDate();
        Long scheduleId =fundTransferDto.getScheduledId() ;

        CustomResponseEntity sender = regex.checkAccountNumberFormat(senderAccountNumber);
        CustomResponseEntity res = regex.checkAccountNumberFormat(receiverAccountNumber);
        if (!sender.isSuccess()) {
//                return sender;
        }
        if (!res.isSuccess()) {
//                return res;
        }

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
            FundTransferDto cbsTransferDto = new FundTransferDto();
            cbsTransferDto.setTransferAmount(Double.valueOf(transferAmount));
            cbsTransferDto.setPurpose(purpose);
            cbsTransferDto.setReceiverAccountNumber(receiverAccountNumber);
            cbsTransferDto.setSenderAccountNumber(senderAccountNumber);
            cbsTransferDto.setBankName(bankName);
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
                    if (senderAccount.isEmpty()) {
//                            return CustomResponseEntity.error("Invalid Account Number");
                    } else if (receiverAccount.isEmpty()) {
//                            return CustomResponseEntity.error("Invalid Receiver account");
                    }
                    if (senderAccount.isPresent() && receiverAccount.isPresent()) {
                        if (senderAccount.get().getTransactionLimit() < cbsTransferDto.getTransferAmount()) {
                            LOGGER.info("Account limit is lower than the transfer money for account : " + senderAccountNumber);
//                                return CustomResponseEntity.error("Account limit is lower than the transfer money");
                        }
                        if (isTransactionAllowed(senderAccount.get().getAccountNumber(), cbsTransferDto.getTransferAmount(), senderAccount.get().getSingleDayLimit()) == false) {
                            LOGGER.info("Single Day Account limit is lower than the transfer money for account : " + senderAccountNumber);
                            throw new SecurityException("Single Day Account limit is lower than the transfer money");
                        }
                        // 2. Apply credit and debit logic
                        double senderBalance = senderAccount.get().getAccountBalance();
                        double receiverBalance = receiverAccount.get().getAccountBalance();
//                            double transferAmount = cbsTransferDto.getTransferAmount();

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
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        String formattedDate = dateFormat.format(date);
                        String input = date.toString();
                        String inputModified = input.replace ( "T" , " " );
//                            String formattedDate = LocalDate.now().format(formatter);
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
                        fundsTransferSender.setTransactionDate(inputModified);
                        HashMap<String, String> map = (HashMap<String, String>) responseDto.getData();
                        fundsTransferSender.setTransactionId(map.get("paymentReference"));
                        fundsTransferSender.setCreditAmt(0.0);
                        fundsTransferSender.setSenderAccount(senderAccount.get().getAccountNumber());
                        fundsTransferSender.setReceiverAccount(receiverAccount.get().getAccountNumber());
                        fundsTransferSender.setCurrency(map.get("ccy"));
                        fundsTransferSender.setIbanCode(senderAccount.get().getIbanCode());
                        fundsTransferSender.setTransactionNarration("Scheduled Payment");
                        // Receiver Transfer Log
                        Transactions fundsTransferReceiver = new Transactions();
                        fundsTransferReceiver.setAccount(receiverAccount.get());
                        fundsTransferReceiver.setCurrentBalance(receiverBalance);
                        fundsTransferReceiver.setCreditAmt(cbsTransferDto.getTransferAmount());
                        fundsTransferReceiver.setTransactionDate(inputModified);
                        fundsTransferReceiver.setTransactionId(map.get("paymentReference"));
                        fundsTransferReceiver.setDebitAmt(0.0);
                        fundsTransferReceiver.setReceiverAccount(receiverAccount.get().getAccountNumber());
                        fundsTransferReceiver.setSenderAccount(senderAccount.get().getAccountNumber());
                        fundsTransferReceiver.setCurrency(map.get("ccy"));
                        fundsTransferReceiver.setIbanCode(receiverAccount.get().getIbanCode());
                        fundsTransferReceiver.setTransactionNarration("Scheduled Payment");

                        // Save both transfer logs
                        transactionsGenericDao.saveOrUpdate(fundsTransferSender);
                        transactionsGenericDao.saveOrUpdate(fundsTransferReceiver);

                        beneficiaryServiceClient.addTransferAmountToBene(receiverAccount.get().getAccountNumber(), String.valueOf(transferAmount), receiverAccount.get().getCustomer().getId());

                        LOGGER.info("Request URL: " + "Transaction done");
                        Optional<ScheduledTransactions> scheduledTransactions = scheduledTransactionsRepository.findById(scheduleId);
                        scheduledTransactions.get().setStatus("Completed");
                        scheduledTransactions.get().setTransactionId(map.get("paymentReference"));
                        scheduledTransactions.get().setCurrency(map.get("ccy"));
                        scheduledTransactionsRepository.save(scheduledTransactions.get());
                        // Return success message
//                            return new CustomResponseEntity<>(responseDto, "Transaction successful.");
                    } else {
                        // Handle missing accounts locally
                        Optional<ScheduledTransactions> scheduledTransactions = scheduledTransactionsRepository.findById(scheduleId);
                        scheduledTransactions.get().setStatus("In-Completed");
                        scheduledTransactionsRepository.save(scheduledTransactions.get());
                        LOGGER.error("Local accounts not found.");
                        throw new SecurityException("Local accounts not found.");
                    }
                } else {
                    Optional<ScheduledTransactions> scheduledTransactions = scheduledTransactionsRepository.findById(scheduleId);
                    scheduledTransactions.get().setStatus("In-Completed");
                    scheduledTransactionsRepository.save(scheduledTransactions.get());
                    // CBS service indicated failure
//                        return CustomResponseEntity.error(responseDto != null ? responseDto.getMessage() : "Unable to Process!");
                }
            } else {
                Optional<ScheduledTransactions> scheduledTransactions = scheduledTransactionsRepository.findById(scheduleId);
                scheduledTransactions.get().setStatus("In-Completed");
                scheduledTransactionsRepository.save(scheduledTransactions.get());
                // Non-200 status response
                LOGGER.error("Unexpected response status: " + response.getStatusCode());
                throw new SecurityException("Unable to Process!");
            }
        } catch (Exception e) {
            Optional<ScheduledTransactions> scheduledTransactions = scheduledTransactionsRepository.findById(scheduleId);
            scheduledTransactions.get().setStatus("In-Completed");
            scheduledTransactionsRepository.save(scheduledTransactions.get());
            // Handle exceptions
            LOGGER.error("Unable to Process Transaction from account !" + senderAccountNumber + " to " + receiverAccountNumber);
            throw new SecurityException("Unable to Process!");
        }
        return null;
    }

}
