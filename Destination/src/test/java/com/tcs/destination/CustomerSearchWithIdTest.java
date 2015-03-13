package com.tcs.destination;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.List;
import org.apache.catalina.connector.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import static org.junit.Assert.assertTrue;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.tcs.destination.DestinationApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.RevenuesResponse;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.controller.CustomerController;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class CustomerSearchWithIdTest {
	

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
		public void TestCustomerController() throws Exception
		{
			this.mockMvc.perform(get("/customer/CUS6").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.customerId").value("CUS6"))
			.andExpect(jsonPath("$.customerName").value("1-800-FLOWERS.COM"))
			.andExpect(jsonPath("$.createdModifiedBy").value("185933"))
			.andExpect(jsonPath("$.documentsAttached").value("YES"))
			.andExpect(jsonPath("$.groupCustomerName").value("1-800-FLOWERS.COM"))
			
//			.andExpect(jsonPath("$.corporateHqAddress").value(""))
//			.andExpect(jsonPath("$.createdModifiedDatetime").value("1424837452182"))
//			.andExpect(jsonPath("$.facebook").value(""))
//			.andExpect(jsonPath("$.logo").value(""))
//			.andExpect(jsonPath("$.website").value(""))
			
			.andExpect(jsonPath("$.connectTs[0].connectId").value("CNN3"))
			.andExpect(jsonPath("$.connectTs[0].connectCategory").value("CUSTOMER"))
			.andExpect(jsonPath("$.connectTs[0].connectName").value("Cloud Connect"))
			.andExpect(jsonPath("$.connectTs[0].connectOpportunityLinkId").value("CNO4"))
			.andExpect(jsonPath("$.connectTs[0].createdModifiedBy").value("734628"))
//			.andExpect(jsonPath("$.connectTs[0].createdModifiedDatetime").value("2015-02-01 09:45:12.0"))
//			.andExpect(jsonPath("$.connectTs[0].dateOfConnect").value("2015-02-01 09:35:42.0"))
			.andExpect(jsonPath("$.connectTs[0].documentsAttached").value("N"))
			.andDo(print())
			.andReturn();
			
			CustomerMasterT CustM=	customerService.findById("CUS6");
			assertNotNull(CustM);
			 assertNotEquals("2015-02-25 16:57:31.91632",CustM.getCreatedModifiedDatetime());
			 assertEquals(null,CustM.getLogo());
			 assertEquals(null,CustM.getCorporateHqAddress());
			 assertEquals(null,CustM.getFacebook());
			 assertEquals(null,CustM.getWebsite());
			 assertEquals("2015-02-01 09:45:12.0",CustM.getConnectTs().get(0).getCreatedModifiedDatetime().toString());
			 assertEquals("2015-02-01 09:35:42.0",CustM.getConnectTs().get(0).getDateOfConnect().toString());
			 assertEquals("N",CustM.getConnectTs().get(0).getDocumentsAttached());
			
		}
		

		@Test
		public void TestCustomerController1() throws Exception
		{
			this.mockMvc.perform(get("/customer/CUS1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andDo(print())
			.andReturn();
             }
	
}
	
	
	