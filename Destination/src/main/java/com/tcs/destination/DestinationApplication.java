package com.tcs.destination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ImportResource("classpath:app-context.xml")
@SpringBootApplication
@EnableJpaRepositories
public class DestinationApplication extends SpringBootServletInitializer {

	private static Class<DestinationApplication> applicationClass = DestinationApplication.class;

	@Autowired
	public static void main(String[] args) {
		// ApplicationContext context = new ClassPathXmlApplicationContext(
		// "file:src/main/resources/META-INF/app-context.xml");
		// LoginService loginService = (LoginService) context
		// .getBean("loginService");
		// //
		// //// Logintable login = new Logintable();
		// //// login.setUserid("sample");
		// //// login.setPasswd("samplepass");
		// //// loginService.addLogin(login);
		// //// System.out.println("Login : " + login + " added successfully");
		// List<Logintable> persons = loginService.fetchAllPersons();
		// System.out.println("The list of all persons = " + persons);
		// System.out.println("************** ENDING PROGRAM *****************");
		SpringApplication.run(DestinationApplication.class, args);
		// CustomerRepository repository =
		// context.getBean(CustomerRepository.class);
		//
		//
		// // fetch all customers
		// Iterable<CustomerMasterT> customers = repository.findAll();
		// System.out.println("Customers found with findAll():");
		// System.out.println("-------------------------------");
		// for (CustomerMasterT customer : customers) {
		// System.out.println(customer);
		// }
		// System.out.println();
		//
		// // fetch an individual customer by ID
		// CustomerMasterT customer = repository.findOne("id");
		// System.out.println("Customer found with findOne(1L):");
		// System.out.println("--------------------------------");
		// System.out.println(customer);
		// System.out.println();
		//
		// // // fetch customers by last name
		// // List<CustomerMasterT> bauers =
		// repository.findByCustomerName(customerName);
		// //
		// System.out.println("Customer found with findByLastName('Bauer'):");
		// //
		// System.out.println("--------------------------------------------");
		// // for (Customer bauer : bauers) {
		// // System.out.println(bauer);
		// // }
		//
		// context.close();

	}

	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}
}
