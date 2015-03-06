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
import com.tcs.destination.controller.CustomerController;
import com.tcs.destination.controller.SearchController;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.service.SearchService;

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
public class SearchController2Test {

	 @Autowired 
	 WebApplicationContext ctx;
	 @Autowired 
	 SearchService searchService;
	 
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new SearchController()).build();
	 

		@Before
		public void setup()
		{
			
			mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
		}
		
		/**
		 * Test Case for valid input {@link com.tcs.destination.controller.SearchController#search(java.lang.String)}.
		 */
		@Test
		public void TestSearchController() throws Exception
		{
			mockMvc.perform(get("/search/CustPart/APOTEKET AB").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].id").value("CUS10"))
			.andExpect(jsonPath("$[0].name").value("APOTEKET AB"))
			.andExpect(jsonPath("$[0].groupCustomerName").value("APOTEKET AB"))
			.andExpect(jsonPath("$[0].geographyMappingT.geography" ).value("Europe"))
			.andExpect(jsonPath("$[0].geographyMappingT.active").value("Y"))
			.andExpect(jsonPath("$[0].geographyMappingT.displayGeography").value("EU&UK"))
			//.andExpect(jsonPath("$[0].logo").value(""))
			//.andExpect(jsonPath("$[0].createdModifiedDatetime").value("2015-02-25 09:40:52.182842"))
			.andExpect(jsonPath("$[0].connects").value(0))
			.andExpect(jsonPath("$[0].opportunities").value(0))
			.andExpect(jsonPath("$[0].entityType").value("Customer"))
			.andDo(print())
			.andReturn();
			List<CustPartResultCard> clist=searchService.searchforCustPartDetail("APOTEKET AB");
			System.out.println(clist.get(0).getCreatedModifiedDatetime());
			 assertNotNull(clist);
			 assertNotEquals("2015-02-25 16:57:31.91632",clist.get(0).getCreatedModifiedDatetime());
			 assertEquals(clist.get(0).getLogo(),null);
			
		}
		
		/**
		 * Test Case for invalid input {@link com.tcs.destination.controller.SearchController#search(java.lang.String)}.
		 * @throws Exception 
		 */
		
		@Test
		public void testSearchCustPart1() throws Exception {
			
			this.mockMvc.perform(get("/search/CustPart/CHANDRA").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andDo(print())
			.andReturn();
			
		}
}
	
	
	
	