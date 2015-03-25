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
import com.tcs.destination.controller.OfferingController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class OfferingControllerTest {


	@Autowired 
	WebApplicationContext ctx;
	
	MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new OfferingController()).build(); 
	
	@Before
	public void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
	}

	@Test
	public void ToTestOfferingMapping() throws Exception {
		mockMvc.perform(get("/offering?fields=offeringId,offering,subSp").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].offeringId").value(1))
		.andExpect(jsonPath("$[0].offering").value("Active Archive"))
		.andExpect(jsonPath("$[0].subSp").value("ABIM - Products"))
		.andExpect(jsonPath("$[1].offeringId").value(2))
		.andExpect(jsonPath("$[1].offering").value("BigData Desktop"))
		.andExpect(jsonPath("$[1].subSp").value("ABIM - Products"))
		.andExpect(jsonPath("$[2].offeringId").value(3))
		.andExpect(jsonPath("$[2].offering").value("Data Migration Tool"))
		.andExpect(jsonPath("$[2].subSp").value("ABIM - Products"))
	    .andDo(print())
		.andReturn();
	}

}
