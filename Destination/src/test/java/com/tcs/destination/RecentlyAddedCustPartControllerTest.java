/**
 * 
 */
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

import com.tcs.destination.controller.RecentlyAddedController;


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
	public final void testRecentlyAdded() throws Exception{

		mockMvc.perform(get("/recent?entityType=CUSTOMER&fields=customerId,createdModifiedBy,"
				+ "documentsAttached,customerName,groupCustomerName").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].customerId").value("CUS614"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("287762"))
			.andExpect(jsonPath("$[0].customerName").value("xoserve  Limited"))
			.andExpect(jsonPath("$[0].documentsAttached").value("YES"))
			.andExpect(jsonPath("$[0].groupCustomerName").value("xoserve  Limited"))
			
			.andExpect(jsonPath("$[4].customerId").value("CUS610"))
			.andExpect(jsonPath("$[4].createdModifiedBy").value("287758"))
			.andExpect(jsonPath("$[4].customerName").value("Walgreen Co."))
			.andExpect(jsonPath("$[4].documentsAttached").value("YES"))
			.andExpect(jsonPath("$[4].groupCustomerName").value("Walgreen"))
			.andDo(print())
			.andReturn();
		
	}

}