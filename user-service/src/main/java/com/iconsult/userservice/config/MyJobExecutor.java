package com.iconsult.userservice.config;


import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.custome.Regex;
import com.iconsult.userservice.feignClient.BeneficiaryServiceClient;
import com.iconsult.userservice.model.dto.request.FundTransferDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.AccountCDDetails;
import com.iconsult.userservice.model.entity.Bank;
import com.iconsult.userservice.model.entity.ScheduledTransactions;
import com.iconsult.userservice.model.entity.Transactions;
import com.iconsult.userservice.repository.AccountCDDetailsRepository;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.repository.ScheduledTransactionsRepository;
import com.iconsult.userservice.repository.TransactionRepository;
import com.iconsult.userservice.service.Impl.FundTransferServiceImpl;
import com.mysql.cj.log.Log;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component

public class MyJobExecutor implements Job {

    private final String getAccountTitleURL = "http://localhost:8081/transaction/fetchAccountTitle";

    private final String fundTransferURL = "http://192.168.0.86:8081/transaction/request";

    private final String interBankFundTransferURL = "http://192.168.0.63:8080/api/v1/1link/creditTransaction";

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
    private AccountRepository accountRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeneficiaryServiceClient beneficiaryServiceClient;
    @Autowired
    private AccountCDDetailsRepository accountCDDetailsRepository;

    @Autowired
    private ScheduledTransactionsRepository scheduledTransactionsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MyJobExecutor.class);

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String senderAccountNumber = (String) jobDataMap.get("senderAccountNumber");
        String receiverAccountNumber = (String) jobDataMap.get("receiverAccountNumber");
        Double transferAmount = (Double) jobDataMap.get("transferAmount");
        String bankName = (String) jobDataMap.get("bankName");
        String purpose = (String) jobDataMap.get("purpose");
        Date date = (Date) jobDataMap.get("date");
        Long scheduleId = (Long) jobDataMap.get("scheduleId");
//            String jobId = (String) jobDataMap.get("jobID");
//            LOGGER.info("Job Started-"+jobId+" at:"+new Date());


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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = dateFormat.format(date);
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
                        fundsTransferSender.setTransactionDate(formattedDate);
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
                        fundsTransferReceiver.setTransactionDate(formattedDate);
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
    }

    public boolean isTransactionAllowed(String account, Double transactionAmount, Double singleDayLimit) {
        Double totalTransactionsForToday = calculateTotalDailyAmount(account);

        if (totalTransactionsForToday + transactionAmount <= singleDayLimit) {
            return true;
        }
        return false;
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
}

