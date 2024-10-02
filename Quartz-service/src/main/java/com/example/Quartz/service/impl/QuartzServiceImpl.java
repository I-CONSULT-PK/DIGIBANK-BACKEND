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
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class QuartzServiceImpl implements QuartzService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzServiceImpl.class);
    private String getAccountUri = "http://localhost:8080/v1/account/getAccount";
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ScheduledTransactionsRepository scheduledTransactionsRepository;

    @Override
    public CustomResponseEntity scheduleFundTransfer(ScheduleFundTransferDto fundTransferDto, String bearerToken) throws SchedulerException {
        try {
            ScheduledTransactions fundsTransferSender = new ScheduledTransactions();
            fundsTransferSender.setCurrentBalance(fundTransferDto.getTransferAmount());
            fundsTransferSender.setDebitAmt(fundTransferDto.getTransferAmount());
            fundsTransferSender.setTransactionDate(fundTransferDto.getLocalDate().toString());
            fundsTransferSender.setCreditAmt(0.0);
            fundsTransferSender.setSenderAccount(fundTransferDto.getSenderAccountNumber());
            fundsTransferSender.setReceiverAccount(fundsTransferSender.getReceiverAccount());
            fundsTransferSender.setTransactionNarration("Scheduled Payment");
            fundsTransferSender.setStatus("In Progress");
            ScheduledTransactions scheduledTransactions = scheduledTransactionsRepository.save(fundsTransferSender);
            fundTransferDto.setScheduledId(scheduledTransactions.getId());

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

            Date startAt = Date.from(fundTransferDto.getLocalDate().atZone(ZoneId.systemDefault()).toInstant());

            Trigger trigger = newTrigger()
                    .withIdentity("triggerIdentity-"+scheduledTransactions.getId())
                    .startAt(startAt)
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60)
                            .withRepeatCount(1)
                            .withMisfireHandlingInstructionNextWithRemainingCount()
                    )
                    .build();


            scheduler.scheduleJob(job, trigger);
            LOGGER.info("Payment Scheduled");
            return new CustomResponseEntity(fundsTransferSender,"Success");
        } catch (Exception exception) {
            LOGGER.info("Account not found! {}", exception.getMessage());
            return CustomResponseEntity.error("Cbs Account not found!");
        }
    }
}
