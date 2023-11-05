package com.example.User_Interaction_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UserInteractionServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserInteractionServiceApplication.class, args);
	}
}
