/**
 * 
 * DestinationApplication.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This DestinationApplication class sets up the configuration and starts the application using spring boot
 * 
 */
@ImportResource("classpath:app-context.xml")
@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
@EnableJpaRepositories(basePackages = "com.tcs.destination.data.repository")
@EntityScan(basePackages = "com.tcs.destination.bean")
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
