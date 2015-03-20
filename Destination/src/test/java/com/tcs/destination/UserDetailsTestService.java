package com.tcs.destination;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.controller.UserDetailsController;
import com.tcs.destination.controller.UserRepositoryUserDetailsService;

@ContextConfiguration({"classpath:app-context.xml" })
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ComponentScan(basePackageClasses=UserRepositoryUserDetailsService.class)
public class UserDetailsTestService {

	@Autowired
	UserRepositoryUserDetailsService userDetailsService;
	
	@Autowired
	ApplicationContext appContext;
	
	@Autowired
    FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	WebApplicationContext wvc1;
	
	MockMvc mockMvcuser	=MockMvcBuilders.standaloneSetup(new UserDetailsController()).build();
	
	@Before
	public void setUp() throws Exception 
	{
		mockMvcuser=MockMvcBuilders.webAppContextSetup(wvc1).addFilters(springSecurityFilterChain).build();
		//userDetailsService = appContext.getBean(UserRepositoryUserDetailsService.class);
	}
	
	@Test
    public void testValidRole() throws Exception
    {
        mockMvcuser.perform(get("/user")
        		.header("Authorization","Basic YWFhOmJiYg==")
        		.accept(MediaType.APPLICATION_JSON))
        	    .andExpect(status().isOk())
        		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        		.andExpect(jsonPath("$.userId").value("886301"))
        		.andExpect(jsonPath("$.supervisorUserId").value("1323232"))
        		;
    }
	
}
