package com.iconsult.service.impl;

import com.iconsult.constants.NotificationChannel;
import com.iconsult.model.NotificationEvent;
import com.iconsult.model.dto.EmailDto;
import com.iconsult.service.NotificationConsumerService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumerServiceImpl implements NotificationConsumerService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(NotificationConsumerServiceImpl.class);

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public CustomResponseEntity consumeNotification(NotificationEvent event) {
        LOGGER.info("Notification event received in email service -> {}", event.toString());

        try {
            NotificationChannel channelType = getNotificationChannel(event.getChannel());

            if (channelType==NotificationChannel.EMAIL) {
                EmailDto emailDto = new EmailDto();
                emailDto.setBody(event.getMessage());
                emailDto.setSubject("DigiBank");
                emailDto.setTo(event.getEmail());

                String emailResponse = sendEmail(emailDto);
                LOGGER.info("Email sent response: {}", emailResponse);

                return new CustomResponseEntity<>("Notification Consumed Successfully");
            }

            return CustomResponseEntity.error("Something went wrong!");


        } catch (Exception e) {
            LOGGER.error("Error processing notification event: {}", e.getMessage(), e);
            throw new RuntimeException("Error consuming notification event", e);  // Ensure the Kafka error handler retries or routes this message to DLT.
        }
    }

    private NotificationChannel getNotificationChannel(String channel) {
        try {
            return NotificationChannel.valueOf(channel.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid notification channel: {}", channel);
            throw new IllegalArgumentException("Invalid notification channel: " + channel, e);
        }
    }

    public String sendEmail(EmailDto emailDto) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(emailDto.getTo());
            mimeMessageHelper.setSubject(emailDto.getSubject());
            mimeMessageHelper.setText(emailDto.getBody());

            javaMailSender.send(mimeMessage);
            LOGGER.info("Message Sent Successfully to: {}", emailDto.getTo());
            return "Email Sent Successfully";

        } catch (Exception e) {
            LOGGER.error("Error sending email to: {} | Error: {}", emailDto.getTo(), e.getMessage(), e);
            throw new RuntimeException("Email sending failed", e);  // Ensure email failure is reported properly.
        }
    }
}
