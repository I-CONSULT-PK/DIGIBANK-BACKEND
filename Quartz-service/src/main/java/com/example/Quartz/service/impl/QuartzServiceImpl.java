package com.example.Quartz.service.impl;


import com.example.Quartz.config.MyJobExecutor;
import com.example.Quartz.model.dto.request.ScheduleFundTransferDto;
import com.example.Quartz.model.dto.response.CbsAccountDto;
import com.example.Quartz.model.entity.ScheduledTransactions;
import com.example.Quartz.repository.ScheduledTransactionsRepository;
import com.example.Quartz.service.QuartzService;

import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class QuartzServiceImpl implements QuartzService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzServiceImpl.class);
    private String getAccountUri = "http://192.168.0.86:8080/v1/account/getAccount";
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ScheduledTransactionsRepository scheduledTransactionsRepository;

    @Override
    public CustomResponseEntity scheduleFundTransfer(ScheduleFundTransferDto fundTransferDto, String bearerToken) throws SchedulerException {
        try {
            // Build the URL with query parameters
            final String bankName = "DIGI Bank";
            URI uri = UriComponentsBuilder.fromHttpUrl(getAccountUri)
                    .queryParam("customerId", fundTransferDto.getCustomerId())
                    .queryParam("accountNumber", fundTransferDto.getSenderAccountNumber())
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri.toString());
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", bearerToken );
            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);


            // Make the HTTP GET request
            ResponseEntity<CbsAccountDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CbsAccountDto.class
            );
            ScheduledTransactions fundsTransferSender = new ScheduledTransactions();
//        fundsTransferSender.setAccountNumber(senderAccount.get());
            fundsTransferSender.setCurrentBalance(response.getBody().getAccountBalance());
            fundsTransferSender.setDebitAmt(fundTransferDto.getTransferAmount());
            fundsTransferSender.setTransactionDate(fundTransferDto.getLocalDate().toString());
//                HashMap<String, String> map = (HashMap<String, String>) responseDto.getData();
//                fundsTransferSender.setTransactionId(map.get("paymentReference"));
            fundsTransferSender.setCreditAmt(0.0);
            fundsTransferSender.setSenderAccount(fundTransferDto.getSenderAccountNumber());
            fundsTransferSender.setReceiverAccount(fundsTransferSender.getReceiverAccount());
//                fundsTransferSender.setCurrency(map.get("ccy"));
            fundsTransferSender.setIbanCode(response.getBody().getIbanCode());
            fundsTransferSender.setTransactionNarration("Scheduled Payment");
            fundsTransferSender.setStatus("In Progress");
            ScheduledTransactions scheduledTransactions = scheduledTransactionsRepository.save(fundsTransferSender);



            JobDataMap jobDataMap = new JobDataMap();
//            jobDataMap.put("senderAccountNumber", fundTransferDto.getSenderAccountNumber());
//            jobDataMap.put("receiverAccountNumber", fundTransferDto.getReceiverAccountNumber());
//            jobDataMap.put("transferAmount", fundTransferDto.getTransferAmount());
//            jobDataMap.put("bankName", fundTransferDto.getBankName());
//            jobDataMap.put("purpose", fundTransferDto.getPurpose());
//            jobDataMap.put("date", fundTransferDto.getLocalDate());
//            jobDataMap.put("scheduleId", scheduledTransactions.getId());
            jobDataMap.put("fundTransferDto", fundTransferDto);

            JobDetail job = newJob(MyJobExecutor.class)
                    .withIdentity("jobIdentity-"+scheduledTransactions.getId())
                    .usingJobData(jobDataMap)
                    .build();
            Trigger trigger = newTrigger()
                    .withIdentity("triggerIdentity-"+scheduledTransactions.getId())
                    .startAt(fundTransferDto.getLocalDate())
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60)
                            .withRepeatCount(1)
                            .withMisfireHandlingInstructionNextWithRemainingCount()
                    )
                    .build();


            scheduler.scheduleJob(job, trigger);
        } catch (Exception exception) {
            LOGGER.info("Account not found!");
            return CustomResponseEntity.error("Cbs Account not found!");
        }


        LOGGER.info("Payment Scheduled");
        return null;
    }
}
