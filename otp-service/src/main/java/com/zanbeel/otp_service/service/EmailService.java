package com.zanbeel.otp_service.service;

import com.zanbeel.otp_service.dto.EmailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    public Boolean sendSimpleMessage(EmailDto emailDTO)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emailDTO.getRecipient());
        mailMessage.setSubject(emailDTO.getSubject());
        mailMessage.setText(emailDTO.getBody());

        Boolean isSent = false;
        try
        {
            emailSender.send(mailMessage);
            isSent = true;
        }
        catch (Exception e) {
            LOGGER.error("Sending e-mail error: {}", e.getMessage());
        }
        return isSent;
    }
}
