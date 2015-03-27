package com.tcs.destination;

import static org.junit.Assert.*;

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
import com.tcs.destination.controller.FrequentlySearchedController;
import com.tcs.destination.controller.OpportunityController;
import com.tcs.destination.controller.UserDetailsController;
import com.tcs.destination.service.OpportunityService;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:app-context.xml")
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@WebAppConfiguration
public class OpportunityControllerTest {
	
	@Autowired
	OpportunityService opportunityService;
	
	@Autowired
	WebApplicationContext wvc;
	
	MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new OpportunityController()).build();
	
	@Before
	public void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(wvc).build();
	}

	@Test
	public void testByOpportunityName() throws Exception {
		mockMvc.perform(get("/opportunity?nameWith=ABM TECH&fields=opportunityId,createdModifiedBy").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.opportunityId").value("OPP2"))
		.andExpect(jsonPath("$.createdModifiedBy").value("232323"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void testOpportunityByRecentUsingCustomerId() throws Exception {
       mockMvc.perform(get("/opportunity/recent?customerId=CUS543&fields=opportunityRequestReceiveDate,opportunityDescription,opportunityName,crmId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].opportunityRequestReceiveDate").value("2014-11-11"))
		.andExpect(jsonPath("$[0].opportunityDescription").value("THIS IS A RETAIL COMPANY"))
		.andExpect(jsonPath("$[0].opportunityName").value("ABM TECH"))
		.andExpect(jsonPath("$[0].crmId").value("12343"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void testByTaskOwnerUsingPrimaryOwner() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?id=886301&role=PRIMARY_OWNER&fields=customerId,opportunityId,crmId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].opportunityId").value("OPP2"))
		.andExpect(jsonPath("$[0].crmId").value("12343"))
		.andExpect(jsonPath("$[0].customerId").value("CUS543"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void testByTaskOwnerUsingSalesSupport() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?id=886301&role=SALES_SUPPORT&fields=customerId,opportunityId,crmId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].opportunityId").value("OPP2"))
		.andExpect(jsonPath("$[0].crmId").value("12343"))
		.andExpect(jsonPath("$[0].customerId").value("CUS543"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void testByTaskOwnerUsingBidOffice() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?id=886301&role=BID_OFFICE&fields=customerId,opportunityId,crmId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].opportunityId").value("OPP2"))
		.andExpect(jsonPath("$[0].crmId").value("12343"))
		.andExpect(jsonPath("$[0].customerId").value("CUS543"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void testByTaskOwnerUsingAll() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?id=886301&role=ALL&fields=opportunityName,bidDetailsTs,bidId,actualBidSubmissionDate,coreAttributesUsedForWinning").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].opportunityName").value("ABM TECH"))
		.andExpect(jsonPath("$[0].bidDetailsTs.bidId").value("BID1"))
		.andExpect(jsonPath("$[0].bidDetailsTs.actualBidSubmissionDate").value("2005-12-12"))
		.andExpect(jsonPath("$[0].bidDetailsTs.coreAttributesUsedForWinning").value("HARD WORK"))
		.andDo(print())
		.andReturn();
	}

}
