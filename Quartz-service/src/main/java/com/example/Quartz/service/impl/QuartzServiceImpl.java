package com.example.Quartz.service.impl;


import com.example.Quartz.config.IbftJobExecutor;
import com.example.Quartz.config.BillPaymentJobExecutor;
import com.example.Quartz.config.MyJobExecutor;
import com.example.Quartz.model.dto.request.ScheduleBillPaymentRequest;
import com.example.Quartz.model.dto.request.ScheduleFundTransferDto;
import com.example.Quartz.model.dto.request.ScheduleIbftFundTransferDto;
import com.example.Quartz.model.dto.response.CbsAccountDto;
import com.example.Quartz.model.entity.ScheduleBillPayment;
import com.example.Quartz.model.entity.ScheduledTransactions;
import com.example.Quartz.repository.SchdeuledBillPaymentRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
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

    @Autowired
    private SchdeuledBillPaymentRepository schdeuledBillPaymentRepository;

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

    @Override
    public CustomResponseEntity scheduleIbftFundTransfer(ScheduleIbftFundTransferDto fundTransferDto, String bearerToken) throws SchedulerException {
        try {
            // Create a new scheduled transaction entity and set its fields
            ScheduledTransactions fundsTransferSender = new ScheduledTransactions();
            fundsTransferSender.setCurrentBalance(fundTransferDto.getAmount());
            fundsTransferSender.setDebitAmt(fundTransferDto.getAmount());
            fundsTransferSender.setTransactionDate(fundTransferDto.getLocalDate().toString());
            fundsTransferSender.setCreditAmt(0.0);
            fundsTransferSender.setSenderAccount(fundTransferDto.getReceiverAccountNumber());
            fundsTransferSender.setReceiverAccount(fundTransferDto.getSenderAccountNumber());
            fundsTransferSender.setTransactionNarration("Scheduled Payment");
            fundsTransferSender.setStatus("In Progress");

            // Save transaction to repository
            ScheduledTransactions scheduledTransactions = scheduledTransactionsRepository.save(fundsTransferSender);
            fundTransferDto.setScheduledId(scheduledTransactions.getId());

            // Create job data map with the DTO
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("fundTransferDto", fundTransferDto);

            // Build the job
            JobDetail job = newJob(IbftJobExecutor.class)
                    .withIdentity("jobIdentity-" + scheduledTransactions.getId())
                    .usingJobData(jobDataMap)
                    .build();

            // Set trigger to start at specified time
            Date startAt = Date.from(fundTransferDto.getLocalDate().atZone(ZoneId.systemDefault()).toInstant());

            Trigger trigger = newTrigger()
                    .withIdentity("triggerIdentity-" + scheduledTransactions.getId())
                    .startAt(startAt)
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60)
                            .withRepeatCount(0)
                            .withMisfireHandlingInstructionNextWithRemainingCount())
                    .build();

            // Schedule the job
            scheduler.scheduleJob(job, trigger);
            LOGGER.info("Payment Scheduled successfully.");

            return new CustomResponseEntity(fundsTransferSender, "Success");

        } catch (Exception exception) {
            LOGGER.error("Account not found or an error occurred: {}", exception.getMessage(), exception);
            return CustomResponseEntity.error("Cbs Account not found!");
        }
    }

    @Override
    public CustomResponseEntity scheduleBillPayment(ScheduleBillPaymentRequest scheduleBillPaymentRequest, String bearerToken) throws SchedulerException {
        try {
        ScheduleBillPayment scheduleBillPayment = new ScheduleBillPayment();
        scheduleBillPayment.setAccountNumber(scheduleBillPaymentRequest.getAccountNumber());
        scheduleBillPayment.setBillId(String.valueOf(scheduleBillPaymentRequest.getBillId()));
        scheduleBillPayment.setUtilityType(scheduleBillPaymentRequest.getUtilityType());
        scheduleBillPayment.setServiceCode(scheduleBillPaymentRequest.getServiceCode());
        scheduleBillPayment.setBillPaymentDate(scheduleBillPaymentRequest.getLocalDate().toString());
        scheduleBillPayment.setStatus("In Progress");
        ScheduleBillPayment scheduleBill= schdeuledBillPaymentRepository.save(scheduleBillPayment);
        scheduleBillPaymentRequest.setScheduledId(scheduleBill.getId());
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("scheduleBillPaymentRequest", scheduleBillPaymentRequest);

        JobDetail job = newJob(BillPaymentJobExecutor.class)
                .withIdentity("jobIdentity-"+scheduleBill.getId())
                .usingJobData(jobDataMap)
                .build();
        Date startAt = Date.from(scheduleBillPaymentRequest.getLocalDate().atZone(ZoneId.systemDefault()).toInstant());
        Trigger trigger = newTrigger()
                .withIdentity("triggerIdentity-"+scheduleBill.getId())
                .startAt(startAt)
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(60)
                        .withRepeatCount(1)
                        .withMisfireHandlingInstructionNextWithRemainingCount()
                )
                .build();
            scheduler.scheduleJob(job, trigger);
            LOGGER.info("Bill Payment Scheduled");
        LOGGER.info("Bill Payment Scheduled");
        return new CustomResponseEntity(scheduleBillPaymentRequest,"Success");
    } catch (Exception exception) {
        LOGGER.info("Bill Payment Failed {}", exception.getMessage());
        return CustomResponseEntity.error("Bill Payment Failed");
    }
    }
}
