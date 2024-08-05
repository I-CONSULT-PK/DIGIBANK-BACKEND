package com.iconsult.userservice.config;


import com.iconsult.userservice.model.entity.Customer;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityConfig {
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Customer customer() {
        return new Customer(); // Example instantiation; adjust as per your actual implementation
    }
    @Bean
    public CustomResponseEntity customResponseEntity(){
        return new CustomResponseEntity();
    }

//    @Bean
//    public CustomResponseEntity Card(){
//        return new CustomResponseEntity();
//    }
}
