package com.zanbeel.otp_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConfigurationProperties
public class EmailConfig {


    @Autowired
    private ConfigurationProvider providerConfiguration;

    @Bean
    public JavaMailSender mailSender()
    {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(providerConfiguration.getHost());
        javaMailSender.setPort(providerConfiguration.getPort());
        javaMailSender.setUsername(providerConfiguration.getUsername());
        javaMailSender.setPassword(providerConfiguration.getPassword());

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", providerConfiguration.getDebug().toString());

        return javaMailSender;
    }
}
