package com.tcs.destination;

import static org.junit.Assert.*;
import java.nio.charset.Charset;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
//import sun.security.acl.PrincipalImpl;
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
import com.tcs.destination.bean.SearchCusPartAjax;
import com.tcs.destination.controller.SearchController;
import com.tcs.destination.service.SearchService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class SearchControllerTest {

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
		 * Test method for valid input{@link com.tcs.destination.controller.SearchController#ajaxSearch(java.lang.String)}.
		 */
		@Test
		public void TestSearchCustPartAjax() throws Exception
		{
			mockMvc.perform(get("/search/ajax/A").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].id").value("CUS7"))
				.andExpect(jsonPath("$[0].name").value("ABN AMRO EU"))
				.andExpect(jsonPath("$[0].entityType").value("Customer"))
				
				.andExpect(jsonPath("$[1].id").value("CUS8"))
				.andExpect(jsonPath("$[1].name").value("ALCATEL-LUCENT INTERNATIONAL"))
				.andExpect(jsonPath("$[1].entityType").value("Customer"))
				
//				.andExpect(jsonPath("$[2].id").value("CUS10"))
//				.andExpect(jsonPath("$[2].name").value("KRAFT FOOD"))
//				.andExpect(jsonPath("$[2].entityType").value("Customer"))
//				
//				.andExpect(jsonPath("$[3].id").value("CUS10"))
//				.andExpect(jsonPath("$[3].name").value("APOTEKET AB"))
//				.andExpect(jsonPath("$[3].entityType").value("Customer"))
//				
//				.andExpect(jsonPath("$[4].id").value("CUS12"))
//				.andExpect(jsonPath("$[4].name").value("ABN Amro EU"))
//				.andExpect(jsonPath("$[4].entityType").value("Customer"))
//				
//				.andExpect(jsonPath("$[5].id").value("PAT6"))
//				.andExpect(jsonPath("$[5].name").value("Apple"))
//				.andExpect(jsonPath("$[5].entityType").value("Partner"))
				.andDo(print())
				.andReturn();
			
				
		}
		/**
		 * Junit Test case for wrong input  {@link com.tcs.destination.controller.SearchController#ajaxSearch(java.lang.String)}.
		 */
		
		
		@Test
		public void TestSearchCustPartAjax1() throws Exception
		{
			mockMvc.perform(get("/search/ajax/j")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound());
			
		}
		
				
		
}

	
