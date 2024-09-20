package com.iconsult.service.impl;

import com.iconsult.model.NotificationEvent;
import com.iconsult.service.NotificationProducerService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducerServiceImpl implements NotificationProducerService {


    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationProducerServiceImpl.class);

    private NewTopic topic;
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;


    public NotificationProducerServiceImpl(NewTopic topic, KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public CustomResponseEntity sendNotification(NotificationEvent event) {

        LOGGER.info(String.format("notification event -> %s ", event.toString()));

        Message<NotificationEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        kafkaTemplate.send(message);

        return new CustomResponseEntity("Notification Sent");
    }
}
