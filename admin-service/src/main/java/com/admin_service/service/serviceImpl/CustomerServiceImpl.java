package com.admin_service.service.serviceImpl;

import com.admin_service.dto.request.CustomerDto;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.*;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {


    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    private final String getCustomers = "http://localhost:8088/v1/customer/getCustomers";
    private final String getActiveCustomers = "http://localhost:8088/v1/customer/getActiveCustomers";


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
}
