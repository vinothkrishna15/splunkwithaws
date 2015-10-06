package com.tcs.destination;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@ImportResource("classpath:app-context.xml")
@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
@EnableBatchProcessing
public class DestinationApplication extends SpringBootServletInitializer {

	private static Class<DestinationApplication> applicationClass = DestinationApplication.class;
	
	@Autowired
	JobRepository jobRepository;

	@Autowired
	public static void main(String[] args) {
		SpringApplication.run(DestinationApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}
	
}
