package com.tcs.destination;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.controller.ConnectController;
import com.tcs.destination.service.ConnectService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class ConnectControllerTest {

	 @Autowired 
	 WebApplicationContext ctx;
	 
	 @Autowired 
	 ConnectService connectService;
	
	 @Autowired
	 FilterChainProxy springSecurityFilterChain;
	 	 
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new ConnectController()).build();
	 MockMvc mockMvc1=MockMvcBuilders.standaloneSetup(new ConnectController()).build();
	

		@Before
		public void setup()
		{
			
			mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
			mockMvc1=MockMvcBuilders.webAppContextSetup(ctx).addFilters(springSecurityFilterChain).build();
		}
		
		/**
		 * Test method for valid input{@link com.tcs.destination.controller.ConnectController#ConnectSearchById(java.lang.String)}.
		 */
		@Test
		public void Test1ConnectById() throws Exception
		{
			mockMvc.perform(get("/connect/CNN3").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.connectId").value("CNN3"))
				.andExpect(jsonPath("$.connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$.connectName").value("Cloud Connect"))
				.andExpect(jsonPath("$.createdModifiedBy").value("734628"))
				.andExpect(jsonPath("$.documentsAttached").value("NO"))
//				.andExpect(jsonPath("$.createdModifiedDatetime").value("2015-02-01 09:45:12"))
//				.andExpect(jsonPath("$.dateOfConnect").value("2015-02-20 09:35:42"))
				.andExpect(jsonPath("$.customerMasterT.customerId").value("CUS542"))
				.andExpect(jsonPath("$.customerMasterT.createdModifiedBy").value("287690"))
				.andExpect(jsonPath("$.customerMasterT.customerName").value("ABN Amro EU"))
				.andExpect(jsonPath("$.customerMasterT.documentsAttached").value("YES"))
				.andExpect(jsonPath("$.customerMasterT.groupCustomerName").value("ABN Amro"))
       			.andDo(print())
				.andReturn();
			ConnectT connect=connectService.searchforConnectsById("CNN3");
				assertNotNull(connect);
				assertEquals("2015-02-01 09:45:12.0", connect.getCreatedModifiedDatetime().toString());
				assertEquals("2015-02-20 09:35:42.0", connect.getEndDatetimeOfConnect().toString());
				
		}
		
		@Test
		public void Test2ConnectById() throws Exception
		{
			mockMvc.perform(get("/connect/CNN3?fields=connectId,connectCategory,connectName").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.connectId").value("CNN3"))
				.andExpect(jsonPath("$.connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$.connectName").value("Cloud Connect"))
				.andDo(print())
				.andReturn();
		}
		
		
		
		@Test
		public void Test3ConnectById() throws Exception
		{
			mockMvc.perform(get("/connect/CNN1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
}
		
		/**
		 * Junit Test case  {@link com.tcs.destination.controller.ConnectController#ConnectSearchByName(java.lang.String)}.
		 */  
		
		
		@Test
		public void Test1ConnectByName() throws Exception
		{
			mockMvc.perform(get("/connect?nameWith=Cloud").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			    .andExpect(jsonPath("$[0].connectId").value("CNN3"))
			    .andExpect(jsonPath("$[0].connectCategory").value("CUSTOMER"))
			    .andExpect(jsonPath("$[0].connectName").value("Cloud Connect"))
			    .andExpect(jsonPath("$[0].createdModifiedBy").value("734628"))
			    .andExpect(jsonPath("$[0].documentsAttached").value("NO"))
//				.andExpect(jsonPath("$[0].createdModifiedDatetime").value("2015-01-20 15:40:35.0"))
//				.andExpect(jsonPath("$[0].dateOfConnect").value("2015-01-20 15:30:45.0"))
			    .andExpect(jsonPath("$[0].customerMasterT.customerId").value("CUS542"))
			    .andExpect(jsonPath("$[0].customerMasterT.createdModifiedBy").value("287690"))
			    .andExpect(jsonPath("$[0].customerMasterT.customerName").value("ABN Amro EU"))
			    .andExpect(jsonPath("$[0].customerMasterT.documentsAttached").value("YES"))
			    .andExpect(jsonPath("$[0].customerMasterT.groupCustomerName").value("ABN Amro"))
			    .andDo(print())
			    .andReturn();
			List<ConnectT> connList=connectService.searchforConnectsByNameContaining("Cloud");
				assertNotNull(connList);
				assertEquals("2015-02-01 09:45:12.0", connList.get(0).getCreatedModifiedDatetime().toString());
				assertEquals("2015-02-20 09:35:42.0", connList.get(0).getEndDatetimeOfConnect().toString());
			}
		
		@Test
		public void Test2ConnectByName() throws Exception
		{
			mockMvc.perform(get("/connect?nameWith=Cloud&fields=ConnectT,connectId,connectCategory,connectName").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].connectId").value("CNN3"))
				.andExpect(jsonPath("$[0].connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$[0].connectName").value("Cloud Connect"))
				.andDo(print())
				.andReturn();
		}
				
		
		@Test
		public void Test3ConnectByName() throws Exception
		{
			mockMvc.perform(get("/connect?nameWith=ABCD").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
}
		
		@Test
		public void Test1ConnectByDate() throws Exception
		{
			mockMvc1.perform(get("/connect/date?from=20012015&to=30012015&owner=PRIMARY&fields=connectId,"
					+ "connectCategory,connectName,createdModifiedBy,primaryOwner").header("Authorization","Basic YWFhOmJiYg==")
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
		}
				
		
}
