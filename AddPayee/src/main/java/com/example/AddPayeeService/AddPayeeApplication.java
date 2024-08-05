package com.example.AddPayeeService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class AddPayeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AddPayeeApplication.class, args);
	}

}
