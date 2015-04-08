package com.tcs.destination;

import java.lang.reflect.Member;
import java.nio.charset.Charset;
import java.util.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.DestinationApplication;
import com.tcs.destination.bean.MyFavorites;
import com.tcs.destination.controller.FavoritesController;
import com.tcs.destination.controller.UserDetailsController;
import com.tcs.destination.service.FavoritesService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:app-context.xml")
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@WebAppConfiguration
public class MyFavoritesControllerTest {

	@Autowired
	FavoritesService myFavService;

	@Autowired
	WebApplicationContext wac;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;

	MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
			new FavoritesController()).build();
	
	MockMvc mockMvc1 = MockMvcBuilders.standaloneSetup(
			new FavoritesController()).build();

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.addFilters(springSecurityFilterChain).build();
		mockMvc1 = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void test() throws Exception {
		mockMvc
				.perform(
						get("/favorites?entityType=CUSTOMER&fields=userFavoritesId,createdDatetime,entityType,customerMasterT,customerId,createdModifiedBy,customerName")
								.header("Authorization", "Basic YWFhOmJiYg==")
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].userFavoritesId").value("USF19"))
				// .andExpect(jsonPath("$.createdDatetime").value("2015-02-03 02:41:06.0"))
				.andExpect(jsonPath("$[0].entityType").value("CUSTOMER"))
				.andExpect(jsonPath("$[0].customerId").value("CUS558"))
				.andDo(print()).andReturn();

		// List<MyFavorites> myfav=myFavService.findFavoritesFor(null,
		// "541045");
		// assertEquals(myfav.get(0).getId(),"CUS6");
		// assertNull(myfav.get(0).getLogo());

		// for negative inputs
		// List<MyFavorites> myfavneg=myFavService.findFavoritesFor("534354");
		// assertNull(myfavneg);

	}

	@Test
	public void TestByCustomerId() throws Exception{
		String requestJson="{ \"entityType\": \"CUSTOMER\", \"customerId\": \"CUS558\", \"contactId\": \"CON1\", \"userId\": \"465897\" }";
				mockMvc.perform(post("/favorites").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestByPartnerId() throws Exception{
		String requestJson="{ \"entityType\": \"PARTNER\", \"partnerId\": \"PAT6\", \"contactId\": \"CON2\", \"userId\": \"353911\" }";
				mockMvc.perform(post("/favorites").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestByConnectId() throws Exception{
		String requestJson="{ \"entityType\": \"CONNECT\", \"connectId\": \"CNN5\", \"contactId\": \"CON3\", \"userId\": \"833389\" }";
				mockMvc.perform(post("/favorites").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestByOpportunityId() throws Exception{
		String requestJson="{ \"entityType\": \"OPPORTUNITY\", \"opportunityId\": \"OPP2\", \"contactId\": \"CON1\", \"userId\": \"541045\" }";
				mockMvc.perform(post("/favorites").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestByDocumentId() throws Exception{
		String requestJson="{ \"entityType\": \"DOCUMENT\", \"documentId\": \"DOC1\", \"contactId\": \"CON1\", \"userId\": \"465897\" }";
				mockMvc.perform(post("/favorites").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	@Test
	public void TestByCustomerIdBadReq() throws Exception{
		String requestJson="{ \"entityType\": \"CUSTOMER\", \"customerId\": \"CUS55\", \"contactId\": \"CON1\", \"userId\": \"465897\" }";
				mockMvc.perform(post("/favorites").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestByPartnerIdBadReq() throws Exception{
		String requestJson="{ \"entityType\": \"PARTNER\", \"partnerId\": \"PAT0\", \"contactId\": \"CON2\", \"userId\": \"353911\" }";
				mockMvc.perform(post("/favorites").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestByConnectIdBadReq() throws Exception{
		String requestJson="{ \"entityType\": \"CONNECT\", \"connectId\": \"CNN0\", \"contactId\": \"CON3\", \"userId\": \"833389\" }";
				mockMvc.perform(post("/favorites").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestByOpportunityIdBadReq() throws Exception{
		String requestJson="{ \"entityType\": \"OPPORTUNITY\", \"opportunityId\": \"OPP0\", \"contactId\": \"CON1\", \"userId\": \"541045\" }";
				mockMvc.perform(post("/favorites").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestByDocumentIdBadReq() throws Exception{
		String requestJson="{ \"entityType\": \"DOCUMENT\", \"documentId\": \"DOC0\", \"contactId\": \"CON1\", \"userId\": \"465897\" }";
				mockMvc.perform(post("/favorites").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest()).andDo(print()).andReturn();
	}
	
	//Ensure that record for userFavoritesId present in user_favorites_t table if there is no record found then change the userfavoritesId and run again.
		@Test
		public void TestDelete() throws Exception {
			mockMvc1.perform(delete("/favorites?userFavoritesId=USF10"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.status").value("Success"));
		}
		
		@Test
		public void TestDeleteInternalServerError() throws Exception {
			mockMvc1.perform(delete("/favorites?userFavoritesId=USF0"))
					.andExpect(status().is5xxServerError())
					.andExpect(jsonPath("$.status").value("Failed"));
		}
}
