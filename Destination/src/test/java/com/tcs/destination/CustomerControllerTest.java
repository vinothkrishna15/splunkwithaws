package com.tcs.destination;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
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

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.controller.CustomerController;
import com.tcs.destination.service.CustomerService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class CustomerControllerTest {
	
	 @Autowired 
	 WebApplicationContext ctx;
	 
	 @Autowired 
	 CustomerService customerService;
	 
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new CustomerController()).build();
	 

		@Before
		public void setup()
		{
			
			mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
		}
		
		
		@Test
		public void Test1CustomerControllerById() throws Exception
		{
			this.mockMvc.perform(get("/customer/CUS541?fields=customerId,customerName,createdModifiedBy,documentsAttached,groupCustomerName").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.customerId").value("CUS541"))
			.andExpect(jsonPath("$.customerName").value("CenturyTel Service Group, LLC"))
			.andExpect(jsonPath("$.createdModifiedBy").value("541045"))
			.andExpect(jsonPath("$.documentsAttached").value("YES"))
			.andExpect(jsonPath("$.groupCustomerName").value("CenturyTel Service Group, LLC"))
			.andDo(print())
			.andReturn();
			}
		
		
		@Test
		public void Test2CustomerControllerById() throws Exception
		{
			this.mockMvc.perform(get("/customer/CUS1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
			}
			
		@Test
		public void Test1CustomerControllerByName() throws Exception
		{
			this.mockMvc.perform(get("/customer?nameWith=1-800-FLOWERS.COM&fields=customerMasterT,customerId,customerName,createdModifiedBy,documentsAttached,groupCustomerName").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].customerId").value("CUS526"))
			.andExpect(jsonPath("$[0].customerName").value("1-800-FLOWERS.COM"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("541045"))
			.andExpect(jsonPath("$[0].documentsAttached").value("YES"))
			.andExpect(jsonPath("$[0].groupCustomerName").value("1-800-FLOWERS.COM"))
			.andDo(print())
			.andReturn();
			}
		
		
		@Test
		public void Test2CustomerControllerByName() throws Exception
		{
			this.mockMvc.perform(get("/customer?nameWith=ABCD").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
			}
		
		
				
		@Test
		public void Test1CustomerControllerTopRevenue() throws Exception
		{
			this.mockMvc.perform(get("/customer/topRevenue?count=4&fields=customerId,createdModifiedBy,customerName,documentsAttached,groupCustomerName,beaconCustomerMappingTs,id,beaconCustomerName,customerGeography,customerMasterT&year=FY'2014-15").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].customerId").value("CUS566"))
			.andExpect(jsonPath("$[0].customerName").value("Lloyds TSB Bank PLC"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("541045"))
			.andExpect(jsonPath("$[0].documentsAttached").value("YES"))
			.andExpect(jsonPath("$[0].groupCustomerName").value("Lloyds TSB Bank PLC"))
			.andExpect(jsonPath("$[0].beaconCustomerMappingTs.id.beaconCustomerName").value("Lloyds TSB Bank PLC"))
			.andExpect(jsonPath("$[0].beaconCustomerMappingTs.id.customerGeography").value("UK"))
			.andExpect(jsonPath("$[0].beaconCustomerMappingTs.customerMasterT").value("CUS566"))
			.andExpect(jsonPath("$[1].customerId").value("CUS581"))
			.andExpect(jsonPath("$[2].customerId").value("CUS538"))
			.andExpect(jsonPath("$[3].customerId").value("CUS596"))
			.andDo(print())
			.andReturn();
			}
		
		
		@Test
		public void Test2CustomerControllerTopRevenue() throws Exception
		{
			this.mockMvc.perform(get("/customer/topRevenue?fields=customerId&year=FY'2014-15").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].customerId").value("CUS581"))
			.andExpect(jsonPath("$[1].customerId").value("CUS538"))
			.andExpect(jsonPath("$[2].customerId").value("CUS596"))
			.andExpect(jsonPath("$[3].customerId").value("CUS566"))
			.andExpect(jsonPath("$[4].customerId").value("CUS550"))
			.andDo(print())
			.andReturn();
			}
		
		@Test
		public void Test1CustomerControllerTargetVsActual() throws Exception
		{
			this.mockMvc.perform(get("/customer/targetVsActual?name=1-800-FLOWERS.COM&year=FY'2014-15").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].target").value(106946.95))
			.andExpect(jsonPath("$[0].actual").value(3002.72))
			.andExpect(jsonPath("$[0].quarter").value("Q2 - 2014-15"))
			.andExpect(jsonPath("$[1].target").value(115173.64))
			.andExpect(jsonPath("$[1].actual").value(9488.0))
			.andExpect(jsonPath("$[1].quarter").value("Q1 - 2014-15"))
			.andDo(print())
			.andReturn();
			}
		
		@Test
		public void Test2CustomerControllerTargetVsActual() throws Exception
		{
			this.mockMvc.perform(get("/customer/targetVsActual?name=ABN").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
			}
		
		@Test
		public void ToTestSearchCustomerByGroupCustomerName() throws Exception
		{
			this.mockMvc.perform(get("/customer/group?nameWith=AB&fields=customerId,customerName").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].customerId").value("CUS527"))
			.andExpect(jsonPath("$[0].customerName").value("ABN Amro EU"))
			.andExpect(jsonPath("$[1].customerId").value("CUS530"))
			.andExpect(jsonPath("$[1].customerName").value("Apoteket AB"))
			.andExpect(jsonPath("$[2].customerId").value("CUS544"))
			.andExpect(jsonPath("$[2].customerName").value("Comcast Cable Communications Management, LLC"))
			.andExpect(jsonPath("$[3].customerId").value("CUS560"))
			.andExpect(jsonPath("$[3].customerName").value("IKEA IT AB"))
			.andExpect(jsonPath("$[4].customerId").value("CUS583"))
			.andExpect(jsonPath("$[4].customerName").value("TeliaSonera AB"))
			.andDo(print())
			.andReturn();
			}
		
		@Test
		public void TestSearchCustomerByGroupCustomerNameNotFound() throws Exception
		{
			this.mockMvc.perform(get("/customer/group?nameWith=ABC&fields=customerId,customerName").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
			}
		
		
}
	
	
	