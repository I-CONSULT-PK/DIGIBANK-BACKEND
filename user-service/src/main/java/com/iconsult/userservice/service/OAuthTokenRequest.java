package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.response.OAuthTokenResponseDTO;
import com.iconsult.userservice.model.entity.Customer;
import com.zanbeel.customUtility.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class OAuthTokenRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthTokenRequest.class);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 8;



    public static OAuthTokenResponseDTO getToken(Customer customer) {
        String clientId = "7QFfRuMBtyfn2x2f0em7MCU_FQIa";
        String clientSecret = "cvbtf0zdP0cff2sHOTJsE8BVguAa";
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encodedAuth);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        // Set the body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", customer.getAccessUserName());  // Use provided username
        //body.add("username", "admin");  // Use provided username
        //body.add("password", "admin");  // Use provided password
        body.add("password", customer.getAccessUserPass());  // Use provided password

        // Create the request entity
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // Initialize RestTemplate and make the POST request
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://localhost:9443/oauth2/token";

        // Exchange the request and get the response
        try {
            ResponseEntity<OAuthTokenResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, OAuthTokenResponseDTO.class);

            if (response.getStatusCode().value() != 200) throw new ServiceException("Unable to Process!");
            return response.getBody();
        } catch (Exception e) {
            LOGGER.error("WS02 may be Down!......... {}", e.getMessage());
            throw new ServiceException("Unable to Process!");
        }
    }
    public static boolean createUser(String userName, String password, String userEmail) {
        // API URL
        String url = "https://localhost:9443/scim2/Users";

        // Basic Auth credentials (replace with your own username:password)
        String auth = "admin:admin";  // Basic Auth: "YWRtaW46YWRtaW4=" is Base64 of "admin:admin"
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;

        // JSON payload for the user creation
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("schemas", new String[]{"urn:ietf:params:scim:schemas:core:2.0:User"});
        requestBody.put("userName", userName);
        requestBody.put("password", password);

        Map<String, Object> email = new HashMap<>();
        email.put("primary", true);
        email.put("value", userEmail);
        email.put("type", "home");

        requestBody.put("emails", new Map[]{email});

        // Headers for the request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authHeader);

        // HttpEntity with request body and headers
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Making the POST request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Check if the status code is 201
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return true;
            }
        } catch (HttpClientErrorException e) {
            // Catch 4xx responses
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                // Handle 409 Conflict response here
                LOGGER.error("Error: " + e.getResponseBodyAsString());
                return false;
            }
            // Other 4xx or 5xx errors can also be handled here
            LOGGER.error(e.getMessage());

        } catch (Exception e) {
            LOGGER.error("WS02 may be Down!.........");
            LOGGER.error(e.getMessage());
        }

        // If we reach here, something went wrong
        return false;
    }

    public static String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
