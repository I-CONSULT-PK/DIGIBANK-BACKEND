package com.admin_service.service.serviceImpl;

import com.admin_service.dto.request.CustomerDto;
import com.admin_service.enumeration.TimePeriod;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.*;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {


    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    private final String getCustomers = "http://localhost:8088/v1/customer/getCustomers";
    private final String getActiveCustomers = "http://localhost:8088/v1/customer/getActiveCustomers";
    private final String getTotalCreditDebit = "http://localhost:8088/v1/customer/fund/getTotalCreditDebit";


    @Override
    public CustomResponseEntity getCustomers() {
        ResponseEntity<CustomerDto> response = null;
        try {
            // Trust all certificates (for testing purposes, NOT recommended for production)
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Initialize SSL context to trust all certificates
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Set up an all-trusting hostname verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(getCustomers)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri.toString());

            // Set headers for the HTTP request
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make HTTP GET request
            ResponseEntity<CustomResponseEntity> res = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CustomResponseEntity.class
            );

            if (res.getStatusCode() == HttpStatus.OK) {
                CustomResponseEntity responseDto = res.getBody();
                if (responseDto != null) {
                    // Print or log responseDto to verify its content
                    LOGGER.info("Received CustomerDto: " + responseDto.toString());
                    return responseDto;
                } else
                    return null;
            } else {
                // Handle error response or non-200 status
                LOGGER.error("Unexpected response status: " + res.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            // Handle exceptions
            LOGGER.error("Exception occurred: ", e);
        }
        return null;
    }

    @Override
    public CustomResponseEntity getActiveCustomers(String action) {
        ResponseEntity<CustomerDto> response = null;
        try {
            // Trust all certificates (for testing purposes, NOT recommended for production)
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Initialize SSL context to trust all certificates
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Set up an all-trusting hostname verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(getActiveCustomers)
                    .queryParam("action", action)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri.toString());

            // Set headers for the HTTP request
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make HTTP GET request
            ResponseEntity<CustomResponseEntity> res = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CustomResponseEntity.class
            );

            if (res.getStatusCode() == HttpStatus.OK) {
                CustomResponseEntity responseDto = res.getBody();
                if (responseDto != null) {
                    // Print or log responseDto to verify its content
                    LOGGER.info("Received CustomerDto: " + responseDto.toString());
                    return responseDto;
                } else
                    return null;
            } else {
                // Handle error response or non-200 status
                LOGGER.error("Unexpected response status: " + res.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            // Handle exceptions
            LOGGER.error("Exception occurred: ", e);
        }
        return null;
    }

    @Override

    public CustomResponseEntity getTotalCreditDebit(String record) {
        try {
            Map<String,Object> time = timePeriod(record);
            String startDate = (String) time.get("startDateStr");
            String endDate  = (String) time.get("endDateStr");
            LOGGER.info("start date : "+startDate);
            LOGGER.info("end date : "+endDate);

            // Build the URI with query parameters
            URI uri = UriComponentsBuilder.fromHttpUrl(getTotalCreditDebit)
                    .queryParam("startDate", startDate)
                    .queryParam("endDate", endDate)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: {}", uri);

            // Set headers (including JWT Authorization if needed)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Add JWT token if required
            // headers.set("Authorization", "Bearer " + "your-jwt-token");
            // Create HttpEntity with headers only (GET requests don't need a body)

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Send GET request and get response
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Handle the response and return the custom response
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return new CustomResponseEntity(response.getBody(), "Success");
            } else {
                return CustomResponseEntity.error("Failed to retrieve data");
            }
        }catch (Exception exception){
        LOGGER.info("Invalid input: " + record + ". Valid options are: 'one day', 'one month', or 'one year'.");
        return CustomResponseEntity.error("Invalid input: "+
                " Valid options for one day: total credit, total debit, type 'one day, " +
                " Valid options for one month: total credit, total debit, type 'one month" +
                " Valid options for one year: total credit, total debit, type 'one year'.");
        }
    }
    public Map<String, Object> timePeriod(String period){

        TimePeriod timePeriod = TimePeriod.fromString(period);

        // Calculate start and end dates
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = timePeriod.calculateStartDate();

        // Format the dates as strings
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);
        Map<String, Object> convertTimePeriod = new HashMap<>();
        convertTimePeriod.put("startDateStr", startDateStr);
        convertTimePeriod.put("endDateStr", endDateStr);
        return  convertTimePeriod;
    }

}

