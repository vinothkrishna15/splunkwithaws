package com.tcs.destination;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import sun.security.acl.PrincipalImpl;

import org.springframework.http.MediaType;

import static org.junit.Assert.assertTrue;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.tcs.destination.DestinationApplication;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.RevenuesResponse;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.controller.CustomerController;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.service.CustomerService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class Top10CustomerTest {

	 @Autowired 
	 WebApplicationContext ctx;
	 
	 @Autowired 
	 CustomerService customerService;
	 
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new CustomerController()).build();
	 

		@Before
		public void setup()
		{
			
			mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
		}
		
	
		
		@Test
		public void TestForTop10Cust() throws Exception
		{
			this.mockMvc.perform(get("/customer/top10").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
//			.andExpect(jsonPath("$[0].logo").value(""))
			.andExpect(jsonPath("$[0].customerName").value("1-800-FLOWERS.COM"))
			.andExpect(jsonPath("$[0].groupCustomerName").value("1-800-FLOWERS.COM"))
			
//			.andExpect(jsonPath("$[1].logo").value(""))
			.andExpect(jsonPath("$[1].customerName").value("ABN AMRO EU"))
			.andExpect(jsonPath("$[1].groupCustomerName").value("ABN AMRO"))
			
			.andDo(print())
			.andReturn();
		List<RevenuesResponse> RList= customerService.findTop10Customers();
			 assertNotNull(RList);
			 assertEquals(null, RList.get(0).getLogo());
			 assertEquals(null, RList.get(1).getLogo());
			 
			 
		}

}
	
	
	
	