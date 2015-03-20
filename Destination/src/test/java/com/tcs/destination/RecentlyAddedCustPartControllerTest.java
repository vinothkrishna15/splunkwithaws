/**
 * 
 */
package com.tcs.destination;
import static org.junit.Assert.*;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.catalina.connector.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.tcs.destination.DestinationApplication;
import com.tcs.destination.bean.MyFavorites;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.controller.FavoritesController;
import com.tcs.destination.controller.RecentlyAddedController;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.service.FavoritesService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.data.repository.CustomerRepository;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class RecentlyAddedCustPartControllerTest {

	 @Autowired 
	 WebApplicationContext ctx;
	 	 
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new RecentlyAddedController()).build();
	 

		@Before
		public void setup()
		{
			
			mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
		}

	/**
	 * Test method for {@link com.tcs.destination.controller.RecentlyAddedController#recentlyAdded()}.
	 */
	@Test
	public final void Test1RecentlyAdded() throws Exception{

		mockMvc.perform(get("/recent?entityType=CUSTOMER&count=2&fields=customerId,createdModifiedBy,documentsAttached,customerName,groupCustomerName,beaconCustomerMappingTs,id,beaconCustomerName,customerGeography,customerMasterT,beaconDataTs,beaconDataId,beaconGeography,financialYear").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			
			.andExpect(jsonPath("$[0].customerId").value("CUS614"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("287762"))
			.andExpect(jsonPath("$[0].groupCustomerName").value("xoserve  Limited"))
			.andExpect(jsonPath("$[0].documentsAttached").value("YES"))
			.andExpect(jsonPath("$[0].customerName").value("xoserve  Limited"))
			
			.andExpect(jsonPath("$[0].beaconCustomerMappingTs.id.beaconCustomerName").value("xoserve  Limited"))
			.andExpect(jsonPath("$[0].beaconCustomerMappingTs.id.customerGeography").value("UK"))
			.andExpect(jsonPath("$[0].beaconCustomerMappingTs.customerMasterT").value("CUS614"))
			
			.andExpect(jsonPath("$[1].customerId").value("CUS613"))
			.andExpect(jsonPath("$[1].createdModifiedBy").value("287761"))
			.andExpect(jsonPath("$[1].groupCustomerName").value("World Bank Group"))
			.andExpect(jsonPath("$[1].documentsAttached").value("YES"))
			.andExpect(jsonPath("$[1].customerName").value("World Bank"))
			
			.andExpect(jsonPath("$[1].beaconCustomerMappingTs.id.beaconCustomerName").value("World Bank"))
			.andExpect(jsonPath("$[1].beaconCustomerMappingTs.id.customerGeography").value("Americas"))
			.andDo(print())
			.andReturn();
		
	}

}