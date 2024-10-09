package com.example.Quartz.config;

import com.example.Quartz.model.dto.request.ScheduleIbftFundTransferDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.quartz.*;
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
public class IbftJobExecutor  implements Job {

    private final String scheduleTransferUrl = "http://localhost:8088/v1/customer/fund/scheduleIbftFundTransfer";

    @Autowired
    RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(IbftJobExecutor.class);

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

        ScheduleIbftFundTransferDto fundTransferDto = (ScheduleIbftFundTransferDto) jobDataMap.get("fundTransferDto");
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
            HttpEntity<ScheduleIbftFundTransferDto> entity = new HttpEntity<>(fundTransferDto, headers);

            // Make HTTP POST request
            ResponseEntity<CustomResponseEntity> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    CustomResponseEntity.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("Scheduled Transaction done from account :" +fundTransferDto.getReceiverAccountNumber()+ " " + fundTransferDto.getSenderAccountNumber());
            }
        } catch (RestClientException e) {
            LOGGER.error("Error occurred while doing  Scheduled Transaction from account :" + e.getMessage());
//            return CustomResponseEntity.error("Unable to process the request." +
//                    " Please verify that the provided information is correct and try again.");
        }

    }
}

