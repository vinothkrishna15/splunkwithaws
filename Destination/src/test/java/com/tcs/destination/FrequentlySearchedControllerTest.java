package com.tcs.destination;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

import com.tcs.destination.controller.FrequentlySearchedController;
import com.tcs.destination.service.FrequentlySearchedService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:app-context.xml")
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@WebAppConfiguration
public class FrequentlySearchedControllerTest {

	
	@Autowired
	FrequentlySearchedService frequentService;
	
	@Autowired
	WebApplicationContext wvc2;
	
	MockMvc mockMvcuser	=MockMvcBuilders.standaloneSetup(new FrequentlySearchedController()).build();
	
	@Before
	public void setUp() throws Exception 
	{
		mockMvcuser=MockMvcBuilders.webAppContextSetup(wvc2).build();
	}

	@Test
	public void test() throws Exception {
		mockMvcuser.perform(get("/frequent?entity=CUSTOMER&fields=count,entity,customerId"
				+ ",corporateHqAddress,createdModifiedBy,createdModifiedDatetime,customerName,"
				+ "documentsAttached,groupCustomerName").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].count").value(2))
		.andExpect(jsonPath("$[0].entity.customerId").value("CUS546"))
		.andExpect(jsonPath("$[0].entity.createdModifiedBy").value("287694"))
		.andExpect(jsonPath("$[0].entity.customerName").value("ASDA Stores Limited"))
		.andExpect(jsonPath("$[0].entity.documentsAttached").value("YES"))
		.andExpect(jsonPath("$[0].entity.groupCustomerName").value("ASDA Stores Limited"))
		.andExpect(jsonPath("$[1].count").value(2))
		.andExpect(jsonPath("$[2].count").value(1))
		.andExpect(jsonPath("$[2].count").value(1))
		.andDo(print())
		.andReturn();
		
		  
	}
	

}
