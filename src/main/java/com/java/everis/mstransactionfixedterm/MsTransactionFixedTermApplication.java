package com.java.everis.mstransactionfixedterm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MsTransactionFixedTermApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTransactionFixedTermApplication.class, args);
	}

}
