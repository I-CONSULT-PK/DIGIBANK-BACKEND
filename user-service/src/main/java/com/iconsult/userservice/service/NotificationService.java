package com.iconsult.userservice.service;

import com.iconsult.userservice.model.entity.NotificationEvent;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final String notificationUrl = "http://localhost:8085/v1/notification/process-notification";

    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(NotificationEvent notificationEvent) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(notificationUrl)
                    .build()
                    .toUri();

            LOGGER.info("Request URL: " + uri);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<NotificationEvent> entity = new HttpEntity<>(notificationEvent, headers);

            restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    CustomResponseEntity.class
            );

        } catch (Exception e) {
            LOGGER.error("Notification Service is down!", e);
            System.out.println("Notification Request failed: " + e.getMessage());
        }
    }
}
