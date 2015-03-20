package com.tcs.destination;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;

import static org.junit.Assert.assertTrue;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.tcs.destination.DestinationApplication;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.controller.ConnectController;
import com.tcs.destination.service.ConnectService;
import com.tcs.destination.service.UserRepositoryUserDetailsService;
import com.tcs.destination.utils.Constants;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class ConnectControllerDateTest {

	 @Autowired 
	 WebApplicationContext ctx;
	 
	 @Autowired 
	 ConnectService connectService;
	 
	 @Autowired
		UserRepositoryUserDetailsService userDetailsService;
		
		@Autowired
		ApplicationContext appContext;
		
		@Autowired
	    FilterChainProxy springSecurityFilterChain;
		
	 
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new ConnectController()).build();
	

		@Before
		public void setup()
		{
			
			mockMvc=MockMvcBuilders.webAppContextSetup(ctx).addFilters(springSecurityFilterChain).build();
		}
		
		/**
		 * Test method for valid input{@link com.tcs.destination.controller.ConnectController#ConnectSearchById(java.lang.String)}.
		 */
		

		@Test
		public void Test1ConnectByDate() throws Exception
		{	
			
			UserDetails userDetails = userDetailsService.loadUserByUsername("aaa");
        Authentication authToken = new UsernamePasswordAuthenticationToken (userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
			mockMvc.perform(get("/connect/date?from=20012015&to=30012015&owner=PRIMARY")
					.header("Authorization","Basic YWFhOmJiYg==")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$[0].connectId").value("CNN2"))
					.andExpect(jsonPath("$[0].connectCategory").value("PARTNER"))
					.andExpect(jsonPath("$[0].connectName").value("DESS Capability Presentation"))
					.andExpect(jsonPath("$[0].createdModifiedBy").value("198054"))
					.andExpect(jsonPath("$[0].primaryOwner").value("541045"))
					.andDo(print())
					.andReturn();
			
//			SimpleDateFormat fmt = new SimpleDateFormat("ddMMyyyy");
//			Date fromDate = fmt.parse("20012015");
//			Date toDate = fmt.parse("30012015");
//			List<ConnectT> connects = connectService.searchforConnectsBetweenForUser(fromDate, toDate,Constants.getUserDetails() , "PRIMARY");
//			assertNotNull(connects);
//			 assertEquals("CNN2",connects.get(0).getConnectId());
//			 
					
				
				}
		
}
