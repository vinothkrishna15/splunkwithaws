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
		.andExpect(jsonPath("$.createdModifiedBy").value("833389"))
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
       mockMvc.perform(get("/opportunity/taskOwner?role=PRIMARY_OWNER&fromDate=2000-12-12&toDate=2016-12-12&id=886301&fields=customerId,opportunityId,crmId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].opportunityId").value("OPP86"))
		.andExpect(jsonPath("$[0].crmId").value("CRM_ID"))
		.andExpect(jsonPath("$[0].customerId").value("CUS526"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void testByTaskOwnerUsingSalesSupport() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?role=SALES_SUPPORT&fromDate=2000-12-12&toDate=2016-12-12&id=886301&fields=customerId,opportunityId,crmId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].opportunityId").value("OPP79"))
		.andExpect(jsonPath("$[0].crmId").value("CRM_ID"))
		.andExpect(jsonPath("$[0].customerId").value("CUS526"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void testByTaskOwnerUsingBidOffice() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?role=BID_OFFICE&fromDate=2000-12-12&toDate=2016-12-12&id=886301&fields=opportunityId,opportunityId,crmId,customerId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].opportunityId").value("OPP88"))
		.andExpect(jsonPath("$[0].crmId").value("CRM_ID"))
		.andExpect(jsonPath("$[0].customerId").value("CUS526"))
		.andExpect(jsonPath("$[1].opportunityId").value("OPP81"))
		.andExpect(jsonPath("$[1].crmId").value("CRM_ID"))
		.andExpect(jsonPath("$[1].customerId").value("CUS527"))
		.andExpect(jsonPath("$[2].opportunityId").value("OPP85"))
		.andExpect(jsonPath("$[2].crmId").value("CRM_ID"))
		.andExpect(jsonPath("$[2].customerId").value("CUS527"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void testByTaskOwnerUsingAll() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?role=ALL&fromDate=2000-12-12&toDate=2016-12-12&id=886301&fields=customerId,opportunityId,crmId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].opportunityId").value("OPP86"))
		.andExpect(jsonPath("$[0].crmId").value("CRM_ID"))
		.andExpect(jsonPath("$[0].customerId").value("CUS526"))
		.andExpect(jsonPath("$[1].opportunityId").value("OPP79"))
		.andExpect(jsonPath("$[1].crmId").value("CRM_ID"))
		.andExpect(jsonPath("$[1].customerId").value("CUS526"))
	    .andExpect(jsonPath("$[2].opportunityId").value("OPP88"))
		.andExpect(jsonPath("$[2].crmId").value("CRM_ID"))
		.andExpect(jsonPath("$[2].customerId").value("CUS526"))
	    .andExpect(jsonPath("$[3].opportunityId").value("OPP81"))
		.andExpect(jsonPath("$[3].crmId").value("CRM_ID"))
		.andExpect(jsonPath("$[3].customerId").value("CUS527"))
	    .andExpect(jsonPath("$[4].opportunityId").value("OPP85"))
		.andExpect(jsonPath("$[4].crmId").value("CRM_ID"))
		.andExpect(jsonPath("$[4].customerId").value("CUS527"))
		.andDo(print())
		.andReturn();
	}

	@Test
	public void testByOpportunityNameNotFound() throws Exception {
		mockMvc.perform(get("/opportunity?nameWith=ABCD").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
	
	@Test
	public void ToTestGetOpportunityUsingId() throws Exception {
       mockMvc.perform(get("/opportunity/OPP2?fields=createdModifiedBy,crmId,customerId,documentsAttached,opportunityName,opportunityRequestReceiveDate").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.createdModifiedBy").value("833389"))
		.andExpect(jsonPath("$.crmId").value("12343"))
		.andExpect(jsonPath("$.customerId").value("CUS543"))
		.andExpect(jsonPath("$.documentsAttached").value("YES"))
		.andExpect(jsonPath("$.opportunityName").value("ABM TECH"))
		.andExpect(jsonPath("$.opportunityRequestReceiveDate").value("2014-11-11"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void testOpportunityByRecentUsingCustomerIdNotFound() throws Exception {
       mockMvc.perform(get("/opportunity/recent?customerId=CUS5000").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
		
	}
	
	@Test
	public void testByTaskOwnerUsingPrimaryOwnerNotFound() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?id=887053&role=PRIMARY_OWNER").accept(MediaType.APPLICATION_JSON))
       .andExpect(status().isNotFound());
	}
	
	@Test
	public void testByTaskOwnerUsingSalesSupportNotFound() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?id=887053&role=SALES_SUPPORT").accept(MediaType.APPLICATION_JSON))
       .andExpect(status().isNotFound());
	}
	
	@Test
	public void testByTaskOwnerUsingBidOfficeNotFound() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?id=887053&role=BID_OFFICE").accept(MediaType.APPLICATION_JSON))
       .andExpect(status().isNotFound());
	}
	
	@Test
	public void testByTaskOwnerUsingAllNotFound() throws Exception {
       mockMvc.perform(get("/opportunity/taskOwner?id=887053&role=ALL").accept(MediaType.APPLICATION_JSON))
       .andExpect(status().isNotFound());
	}

}
