package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.model.dto.response.FetchAccountDto;
import com.iconsult.userservice.model.entity.Bank;
import com.iconsult.userservice.model.entity.Card;
import com.iconsult.userservice.model.entity.Customer;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FundTransferServiceImpl implements FundTransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FundTransferServiceImpl.class);

    private final String getAccountTitleURL = "http://localhost:8081/transaction/fetchAccountTitle";

    @Autowired
    private GenericDao<Bank> bankGenericDao;

    @Autowired
    private RestTemplate restTemplate;

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
            URI uri = UriComponentsBuilder.fromHttpUrl(getAccountTitleURL)
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
}
