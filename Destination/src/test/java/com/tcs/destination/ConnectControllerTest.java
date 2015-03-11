package com.tcs.destination;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;

import static org.junit.Assert.assertTrue;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.tcs.destination.DestinationApplication;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.controller.ConnectController;
import com.tcs.destination.service.ConnectService;



@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class ConnectControllerTest {

	 @Autowired 
	 WebApplicationContext ctx;
	 
	 @Autowired 
	 ConnectService connectService;
	 
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new ConnectController()).build();
	

		@Before
		public void setup()
		{
			
			mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
		}
		
		/**
		 * Test method for valid input{@link com.tcs.destination.controller.ConnectController#ajaxSearch(java.lang.String)}.
		 */
		@Test
		public void TestConnectAjax() throws Exception
		{
			mockMvc.perform(get("/connect/search?typed=A").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].connectId").value("CNN2"))
				.andExpect(jsonPath("$[0].connectCategory").value("PARTNER"))
				.andExpect(jsonPath("$[0].connectName").value("DESS Capability Presentation"))
				.andExpect(jsonPath("$[0].connectOpportunityLinkId").value("CNO5"))
				.andExpect(jsonPath("$[0].createdModifiedBy").value("198054"))
//				.andExpect(jsonPath("$[0].createdModifiedDatetime").value("2015-01-20 15:40:35.0"))
//				.andExpect(jsonPath("$[0].dateOfConnect").value("2015-01-20 15:30:45.0"))
				.andExpect(jsonPath("$[0].documentsAttached").value("Y"))
								
				.andDo(print())
				.andReturn();
			List<ConnectT> wlist=connectService.searchforConnectsByName("A");
			assertNotNull(wlist);
			 assertEquals("2015-01-20 15:40:35.0", wlist.get(0).getCreatedModifiedDatetime().toString());
			 assertEquals("2015-01-20 15:30:45.0", wlist.get(0).getDateOfConnect().toString());
				
		}
		/**
		 * Junit Test case for wrong input  {@link com.tcs.destination.controller.ConnectController#ajaxSearch(java.lang.String)}.
		 */
		
		
		@Test
		public void TestConnectAjax1() throws Exception
		{
			mockMvc.perform(get("/connect/search?typed=j").accept(MediaType.APPLICATION_JSON))
				
				.andExpect(status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));
				List<ConnectT> wlist=connectService.searchforConnectsByName("j");
				System.out.println("wrong Input");
				assertTrue(wlist.isEmpty());
				System.out.println(wlist);
			
		}
		
				
		
}

	
