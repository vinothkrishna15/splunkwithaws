package com.tcs.destination;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
	
	MockMvc mockMvc	=MockMvcBuilders.standaloneSetup(new FrequentlySearchedController()).build();
	
	@Before
	public void setUp() throws Exception 
	{
		mockMvc=MockMvcBuilders.webAppContextSetup(wvc2).build();
	}

	@Test
	public void TestFrequentByCustomer() throws Exception {
		mockMvc.perform(get("/frequent?entityType=CUSTOMER&fields=count,entity,customerId"
				+ ",corporateHqAddress,createdModifiedBy,createdModifiedDatetime,customerName,"
				+ "documentsAttached,groupCustomerName").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].entity.customerId").value("CUS546"))
		.andExpect(jsonPath("$[0].entity.createdModifiedBy").value("287694"))
		.andExpect(jsonPath("$[0].entity.customerName").value("ASDA Stores Limited"))
		.andExpect(jsonPath("$[0].entity.documentsAttached").value("YES"))
		.andExpect(jsonPath("$[0].entity.groupCustomerName").value("ASDA Stores Limited"))
		.andDo(print())
		.andReturn();
		}
	
	@Test
	public void TestFrequentByPartner() throws Exception {
		mockMvc.perform(get("/frequent?entityType=PARTNER&fields=count,entity,"
				+ "partnerId,corporateHqAddress,createdModifiedBy,createdModifiedDatetime,"
				+ "partnerName,documentsAttached").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].entity.partnerId").value("PAT6"))
		.andExpect(jsonPath("$[0].entity.createdModifiedBy").value("278648"))
		.andExpect(jsonPath("$[0].entity.partnerName").value("Apple"))
		.andExpect(jsonPath("$[0].entity.documentsAttached").value("NO"))
		.andDo(print())
		.andReturn();
		
		  
	}
	
	@Test
	public void TestForInsertCust() throws Exception {
		
		String requestJson="{\"entityType\":\"CUSTOMER\",\"entityId\":\"CUS546\",\"userId\":\"833389\"}";
		
			this.mockMvc.perform(post("/frequent")
					.content(requestJson)
					.header("Authorization", "Basic YWFhOmJiYg==")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
	           	.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print()).andReturn();	
			}
	
	@Test
	public void TestForInsertPart() throws Exception {
		
		String requestJson="{\"entityType\":\"PARTNER\",\"entityId\":\"PAT4\",\"userId\":\"541045\"}";
		
			this.mockMvc.perform(post("/frequent")
					.content(requestJson)
					.header("Authorization", "Basic YWFhOmJiYg==")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
	           	.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print()).andReturn();	
			}
	
//	@Test
//	public void TestForInsertCustBadReq() throws Exception {
//		
//		String requestJson="{\"entityType\":\"CUSTOMER\",\"entityId\":\"CUS5000\",\"userId\":\"833389\"}";
//		
//			this.mockMvc.perform(post("/frequent")
//					.content(requestJson)
//					.header("Authorization", "Basic YWFhOmJiYg==")
//				.contentType(TestUtil.APPLICATION_JSON_UTF8)
//	           	.accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isBadRequest());	
//			}
//	
//	@Test
//	public void TestForInsertPartBadReq() throws Exception {
//		
//		String requestJson="{\"entityType\":\"PARTNER\",\"entityId\":\"PAT4000\",\"userId\":\"541045\"}";
//		
//			this.mockMvc.perform(post("/frequent")
//					.content(requestJson)
//					.header("Authorization", "Basic YWFhOmJiYg==")
//				.contentType(TestUtil.APPLICATION_JSON_UTF8)
//	           	.accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isBadRequest());
//			}

}
