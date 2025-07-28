package com.example.runway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RunwayApplication {
	public static void main(String[] args) {
		SpringApplication.run(RunwayApplication.class, args);
	}

}
