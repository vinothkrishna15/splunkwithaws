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

import com.tcs.destination.controller.CountryController;
import com.tcs.destination.controller.CurrencyController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class CurrencyControllerTest {
	
	@Autowired 
	WebApplicationContext ctx;
	
	MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new CurrencyController()).build(); 

	@Before
	public void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
	}

	@Test
	public void ToTestCurrencyConverter() throws Exception {
		mockMvc.perform(get("/currency?from=USD&to=Dollar&value=989").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.status").value("SUCCESS"))
		.andExpect(jsonPath("$.description").value("2863.70"))
	    .andDo(print())
		.andReturn();
	}


}
