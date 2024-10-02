package com.example.Quartz.config;


import com.example.Quartz.model.dto.request.ScheduleFundTransferDto;

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
import org.springframework.web.client.RestClientException;
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
    @Autowired
    RestTemplate restTemplate;

    private final String getAccountTitleURL = "http://localhost:8081/transaction/fetchAccountTitle";

    private final String fundTransferURL = "http://192.168.0.86:8081/transaction/request";

    private final String scheduleTransferUrl = "http://localhost:8088/v1/customer/fund/scheduleFundTransfer";


    private static final Logger LOGGER = LoggerFactory.getLogger(MyJobExecutor.class);

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
//        String senderAccountNumber = (String) jobDataMap.get("senderAccountNumber");
//        String receiverAccountNumber = (String) jobDataMap.get("receiverAccountNumber");
//        Double transferAmount = (Double) jobDataMap.get("transferAmount");
//        String bankName = (String) jobDataMap.get("bankName");
//        String purpose = (String) jobDataMap.get("purpose");
//        Date date = (Date) jobDataMap.get("date");
//        Long scheduleId = (Long) jobDataMap.get("scheduleId");
        ScheduleFundTransferDto fundTransferDto = (ScheduleFundTransferDto) jobDataMap.get("fundTransferDto");
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(scheduleTransferUrl)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Create HttpEntity with Cbs_TransferDto as the body and headers
            HttpEntity<ScheduleFundTransferDto> entity = new HttpEntity<>(fundTransferDto, headers);

            // Make HTTP POST request
            ResponseEntity<CustomResponseEntity> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    CustomResponseEntity.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("Scheduled Transaction done from account :" +fundTransferDto.getSenderAccountNumber()+ " " + fundTransferDto.getReceiverAccountNumber());
            }
        } catch (RestClientException e) {
            LOGGER.error("Error occurred while doing  Scheduled Transaction from account :" + e.getMessage());
//            return CustomResponseEntity.error("Unable to process the request." +
//                    " Please verify that the provided information is correct and try again.");
        }

    }

}

