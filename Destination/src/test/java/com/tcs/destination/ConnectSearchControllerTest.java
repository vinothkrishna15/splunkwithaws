package com.tcs.destination;

import java.lang.reflect.Member;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.DestinationApplication;
import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.controller.ConnectSearchController;
import com.tcs.destination.controller.FrequentlySearchedController;
import com.tcs.destination.controller.UserDetailsController;
import com.tcs.destination.service.FrequentlySearchedCustPartService;
import com.tcs.destination.service.SearchService;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/app-context.xml")
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@WebAppConfiguration
public class ConnectSearchControllerTest {
	
	
	@Autowired
	SearchService searchService;
	
	@Autowired
	WebApplicationContext wvc7;
	
	MockMvc mockMvcuser=MockMvcBuilders.standaloneSetup(new ConnectSearchController()).build();
	
	
	@Before
	public void setUp() throws Exception {
		mockMvcuser=MockMvcBuilders.webAppContextSetup(wvc7).build();
	}

	@Test
	public void test() throws Exception {
		mockMvcuser.perform(get("/search/ConnectPart/Cloud").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].connectId").value("CNN3"))
		.andExpect(jsonPath("$[0].connectCategory").value("CUSTOMER"))
		.andExpect(jsonPath("$[0].connectName").value("Cloud Connect"))
		.andExpect(jsonPath("$[0].connectOpportunityLinkId").value("CNO4"))
		.andExpect(jsonPath("$[0].documentAttached").value("N"))
//		.andExpect(jsonPath("$[0].geographyMappingT.displayGeography").value("EU&UK"))
//		.andExpect(jsonPath("$[1].id").value("PAT4"))
//		.andExpect(jsonPath("$[1].name").value("Microsoft"))
		.andDo(print())
		.andReturn();
	}

}
