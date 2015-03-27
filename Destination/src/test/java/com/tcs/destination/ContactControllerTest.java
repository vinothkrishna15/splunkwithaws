package com.tcs.destination;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.controller.ContactController;
import com.tcs.destination.service.ContactService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class ContactControllerTest {

	 @Autowired 
	 WebApplicationContext ctx;
	 
	 @Autowired 
	 ContactService contactService;
	
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new ContactController()).build();
	
	
	
	@Before
	public void setUp() throws Exception {
		
		mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
	}

	
	
	@Test
	public void Test1ContactByCustomerIdContactName() throws Exception
	{
		mockMvc.perform(get("/contact?customerId=CUS545&nameWith=KUMAR&fields=contactId,contactCategory,contactName,contactType,createdModifiedBy,employeeNumber").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].contactId").value("CON1"))
			.andExpect(jsonPath("$[0].contactCategory").value("CUSTOMER"))
			.andExpect(jsonPath("$[0].contactName").value("Anoop Kumar"))
			.andExpect(jsonPath("$[0].contactType").value("Internal"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("734628"))
			.andExpect(jsonPath("$[0].employeeNumber").value("125678"))
			.andDo(print())
			.andReturn();
	}
	
	@Test
	public void Test2ContactByCustomerIdContactName() throws Exception
	{
		mockMvc.perform(get("/contact?customerId=CUS54")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is5xxServerError());
	}
	
	
	@Test
	public void Test1ContactByPartnerIdContactName() throws Exception
	{
		mockMvc.perform(get("/contact?partnerId=PAT4&nameWith=Satya&fields=contactId,"
				+ "contactCategory,contactName,contactType,createdModifiedBy,employeeNumber")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].contactId").value("CON2"))
			.andExpect(jsonPath("$[0].contactCategory").value("PARTNER"))
			.andExpect(jsonPath("$[0].contactName").value("Satya Nadella"))
			.andExpect(jsonPath("$[0].contactType").value("External"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("278648"))
			.andExpect(jsonPath("$[0].employeeNumber").value("018323"))
			.andDo(print())
			.andReturn();
	}
	
	@Test
	public void Test2ContactByPartnerIdContactName() throws Exception
	{
		mockMvc.perform(get("/contact?partnerId=PAT1")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is5xxServerError());
	}
	
	@Test
	public void Test1ContactByCustomerId() throws Exception
	{
		mockMvc.perform(get("/contact?customerId=CUS545&fields=contactId,contactCategory,contactName,contactType,createdModifiedBy,employeeNumber").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].contactId").value("CON1"))
			.andExpect(jsonPath("$[0].contactCategory").value("CUSTOMER"))
			.andExpect(jsonPath("$[0].contactName").value("Anoop Kumar"))
			.andExpect(jsonPath("$[0].contactType").value("Internal"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("734628"))
			.andExpect(jsonPath("$[0].employeeNumber").value("125678"))
			.andDo(print())
			.andReturn();
	}
	
	@Test
	public void Test1ContactByPartnerId() throws Exception
	{
		mockMvc.perform(get("/contact?partnerId=PAT4&fields=contactId,"
				+ "contactCategory,contactName,contactType,createdModifiedBy,employeeNumber")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].contactId").value("CON2"))
			.andExpect(jsonPath("$[0].contactCategory").value("PARTNER"))
			.andExpect(jsonPath("$[0].contactName").value("Satya Nadella"))
			.andExpect(jsonPath("$[0].contactType").value("External"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("278648"))
			.andExpect(jsonPath("$[0].employeeNumber").value("018323"))
			.andDo(print())
			.andReturn();
	}
 }

