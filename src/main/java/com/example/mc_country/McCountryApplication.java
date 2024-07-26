package com.example.mc_country;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class McCountryApplication {

	public static void main(String[] args) {
		SpringApplication.run(McCountryApplication.class, args);
	}

}
