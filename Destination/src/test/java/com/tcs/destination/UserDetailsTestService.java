package com.tcs.destination;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.controller.UserRepositoryUserDetailsService;
import com.tcs.destination.controller.UserDetailsController;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.service.UserService;

@ContextConfiguration({ "classpath:app-context.xml" })
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ComponentScan(basePackageClasses = UserRepositoryUserDetailsService.class)
public class UserDetailsTestService {

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserRepositoryUserDetailsService userDetailsService;

	@Autowired
	ApplicationContext appContext;

	@Autowired
	FilterChainProxy springSecurityFilterChain;

	@Autowired
	WebApplicationContext wvc1;

	MockMvc mockMvcuser = MockMvcBuilders.standaloneSetup(
			new UserDetailsController()).build();

	@Before
	public void setUp() throws Exception {
		mockMvcuser = MockMvcBuilders.webAppContextSetup(wvc1)
				.addFilters(springSecurityFilterChain).build();
		// userDetailsService =
		// appContext.getBean(UserRepositoryUserDetailsService.class);
	}

	@Test
	public void testValidRole() throws Exception {

		Boolean val = true;
		mockMvcuser
				.perform(
						get("/user").header("Authorization",
								"Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.userId").value("886301"))
				.andExpect(jsonPath("$.supervisorUserId").value("1323232"))
				.andExpect(jsonPath("$.tempPassword").value("bbb"))
				.andExpect(
						jsonPath("$.userEmailId").value(
								"gopikrish.rgk03@gmail.com"))
				.andExpect(jsonPath("$.userGeography").value("India"))
				.andExpect(jsonPath("$.userName").value("aaa"))
				.andExpect(jsonPath("$.userTelephone").value("9894950547"))
				.andExpect(jsonPath("$.accountNonExpired").value(val))
				.andExpect(jsonPath("$.accountNonLocked").value(val))
				.andExpect(jsonPath("$.credentialsNonExpired").value(val))
				.andExpect(jsonPath("$.enabled").value(val)).andReturn();
	}

	@Test
	public void toTestSearchUserByName() throws Exception {
		mockMvcuser
				.perform(
						get("/user?nameWith=aaa").header("Authorization",
								"Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].userId").value("886301"))
				.andExpect(jsonPath("$[0].supervisorUserId").value("1323232"))
				.andExpect(
						jsonPath("$[0].supervisorUserName").value(
								"parthiv patel"))
				.andExpect(jsonPath("$[0].tempPassword").value("bbb"))
				.andExpect(
						jsonPath("$[0].userEmailId").value(
								"gopikrish.rgk03@gmail.com"))
				.andExpect(jsonPath("$[0].userGeography").value("India"))
				.andExpect(jsonPath("$[0].userName").value("aaa"))
				.andExpect(jsonPath("$[0].userTelephone").value("9894950547"))
				.andReturn();

	}

	@Test
	public void toTestForInvalidInputs() throws Exception {
		mockMvcuser
				.perform(get("/user/naqme?nameWith=nffghg").header(
								"Authorization", "Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
	}
}
