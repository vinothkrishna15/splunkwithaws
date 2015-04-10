package com.tcs.destination;

import static org.junit.Assert.*;
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

import com.tcs.destination.controller.CompetitorController;
import com.tcs.destination.controller.CustomerController;
import com.tcs.destination.service.CompetitorService;
import com.tcs.destination.service.CustomerService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class CompetitorControllerTest {
	
	 @Autowired 
	 WebApplicationContext ctx;
	 
	 @Autowired 
	 CompetitorService competitorService;
	
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new CompetitorController()).build();

	@Before
	public void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
	}

	@Test
	public void TestCompetitorControllerByName() throws Exception
	{
		this.mockMvc.perform(get("/competitor?nameWith=A&fields=competitorName,activeFlag").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].competitorName").value("Accenture"))
		.andExpect(jsonPath("$[0].activeFlag").value("Y"))
		.andExpect(jsonPath("$[1].competitorName").value("Adobe"))
		.andExpect(jsonPath("$[1].activeFlag").value("Y"))
		.andExpect(jsonPath("$[2].competitorName").value("Affecto"))
		.andExpect(jsonPath("$[2].activeFlag").value("Y"))
		.andExpect(jsonPath("$[3].competitorName").value("Agility works"))
		.andExpect(jsonPath("$[3].activeFlag").value("Y"))
		.andDo(print())
		.andReturn();
		}
	@Test
	public void Test2ContactByCustomerIdContactName() throws Exception
	{
		mockMvc.perform(get("/competitor?nameWith=zwewe")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

}
