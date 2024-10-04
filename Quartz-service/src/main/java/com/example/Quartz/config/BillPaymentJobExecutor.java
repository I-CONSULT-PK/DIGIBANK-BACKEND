package com.example.Quartz.config;

import com.example.Quartz.model.dto.request.ScheduleBillPaymentRequest;
import com.example.Quartz.model.dto.request.ScheduleFundTransferDto;
import com.example.Quartz.model.entity.ScheduleBillPayment;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@Component
public class BillPaymentJobExecutor implements Job {

    private final String scheduleTransferUrl = "http://localhost:8088/v1/billPayment/scheduleBillPay";

    @Autowired
    RestTemplate restTemplate;

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
        ScheduleBillPaymentRequest scheduleBillPaymentRequest = (ScheduleBillPaymentRequest) jobDataMap.get("scheduleBillPaymentRequest");
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
            HttpEntity<ScheduleBillPaymentRequest> entity = new HttpEntity<>(scheduleBillPaymentRequest, headers);

            // Make HTTP POST request
            ResponseEntity<CustomResponseEntity> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    CustomResponseEntity.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("Scheduled Transaction done from account :" +scheduleBillPaymentRequest.getAccountNumber()+ " " + scheduleBillPaymentRequest.getAccountNumber());
            }
        } catch (RestClientException e) {
            LOGGER.error("Error occurred while doing  Scheduled Transaction from account :" + e.getMessage());
//            return CustomResponseEntity.error("Unable to process the request." +
//                    " Please verify that the provided information is correct and try again.");
        }

    }

}
