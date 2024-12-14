package com.example.oqp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.oqp.db.repository")
@EntityScan(basePackages = "com.example.oqp.db.entity")
@EnableJpaAuditing
public class OqpApplication {

	public static void main(String[] args) {
		SpringApplication.run(OqpApplication.class, args);
	}

}
