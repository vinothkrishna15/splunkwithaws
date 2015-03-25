package com.tcs.destination;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.controller.ConnectController;
import com.tcs.destination.controller.UserRepositoryUserDetailsService;
import com.tcs.destination.service.ConnectService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
@ComponentScan(basePackageClasses=UserRepositoryUserDetailsService.class)
public class ConnectControllerTest {

	 @Autowired 
	 WebApplicationContext ctx;
	 
	 @Autowired 
	 ConnectService connectService;
	
	 @Autowired
	 FilterChainProxy springSecurityFilterChain;
	 
	 @Autowired
	 UserRepositoryUserDetailsService userDetailsService;
	 	 
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new ConnectController()).build();
	 MockMvc mockMvc1=MockMvcBuilders.standaloneSetup(new ConnectController()).build();
	

		@Before
		public void setup()
		{
			
			mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
			mockMvc1=MockMvcBuilders.webAppContextSetup(ctx).addFilters(springSecurityFilterChain).build();
		}
		
				
		@Test
		public void Test1ConnectById() throws Exception
		{
			mockMvc.perform(get("/connect/CNN3?fields=connectId,connectCategory,connectName").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.connectId").value("CNN3"))
				.andExpect(jsonPath("$.connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$.connectName").value("Cloud Connect"))
				.andDo(print())
				.andReturn();
		}
				
		
		@Test
		public void Test2ConnectById() throws Exception
		{
			mockMvc.perform(get("/connect/CNN10").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
}
				
		@Test
		public void Test1ConnectByName() throws Exception
		{
			mockMvc.perform(get("/connect?nameWith=Cloud&fields=ConnectT,connectId,connectCategory,connectName").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].connectId").value("CNN3"))
				.andExpect(jsonPath("$[0].connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$[0].connectName").value("Cloud Connect"))
				.andDo(print())
				.andReturn();
		}
				
		
		@Test
		public void Test2ConnectByName() throws Exception
		{
			mockMvc.perform(get("/connect?nameWith=ABCD").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
}
		
		@Test
		public void TestConnectByDateUserIdCustPartId() throws Exception
		{
			mockMvc1.perform(get("/connect/date?from=20012015&to=30012015&userId=541045&customerId=CUS541&partnerId=PAT4&fields=connectId,"
					+ "connectCategory,connectName,createdModifiedBy,documentsAttached,primaryOwner,country")
					.header("Authorization","Basic YWFhOmJiYg==")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$[0].connectId").value("CNN2"))
					.andExpect(jsonPath("$[0].connectCategory").value("PARTNER"))
					.andExpect(jsonPath("$[0].connectName").value("DESS Capability Presentation"))
					.andExpect(jsonPath("$[0].createdModifiedBy").value("198054"))
					.andExpect(jsonPath("$[0].documentsAttached").value("YES"))
					.andExpect(jsonPath("$[0].primaryOwner").value("541045"))
					.andExpect(jsonPath("$[0].country").value("Netherlands"))
					.andDo(print()).andReturn();
		}
		
		@Test
		public void TestConnectByDateCustId() throws Exception
		{
			mockMvc1.perform(get("/connect/date?from=01022015&to=20022015&customerId=CUS542&fields=connectId,"
					+ "connectCategory,connectName,createdModifiedBy,documentsAttached,primaryOwner,partnerId,country")
					.header("Authorization","Basic YWFhOmJiYg==")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$[0].connectId").value("CNN3"))
					.andExpect(jsonPath("$[0].connectCategory").value("CUSTOMER"))
					.andExpect(jsonPath("$[0].connectName").value("Cloud Connect"))
					.andExpect(jsonPath("$[0].createdModifiedBy").value("734628"))
					.andExpect(jsonPath("$[0].documentsAttached").value("NO"))
					.andExpect(jsonPath("$[0].primaryOwner").value("353911"))
					.andExpect(jsonPath("$[0].country").value("USA"))
					.andExpect(jsonPath("$[0].partnerId").value("PAT5"))
					.andDo(print()).andReturn();
		}
		@Test
		public void TestConnectByDatePartId() throws Exception
		{
			mockMvc1.perform(get("/connect/date?from=01022015&to=20022015&partnerId=PAT5&fields=connectId,connectCategory,"
					+ "connectName,createdModifiedBy,documentsAttached,primaryOwner,customerId,partnerId,country")
					.header("Authorization","Basic YWFhOmJiYg==")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$[0].connectId").value("CNN3"))
					.andExpect(jsonPath("$[0].connectCategory").value("CUSTOMER"))
					.andExpect(jsonPath("$[0].connectName").value("Cloud Connect"))
					.andExpect(jsonPath("$[0].createdModifiedBy").value("734628"))
					.andExpect(jsonPath("$[0].documentsAttached").value("NO"))
					.andExpect(jsonPath("$[0].primaryOwner").value("353911"))
					.andExpect(jsonPath("$[0].country").value("USA"))
					.andExpect(jsonPath("$[0].customerId").value("CUS542"))
					.andExpect(jsonPath("$[0].partnerId").value("PAT5"))
					
					.andExpect(jsonPath("$[1].connectId").value("CNN6"))
					.andExpect(jsonPath("$[1].connectCategory").value("PARTNER"))
					.andExpect(jsonPath("$[1].connectName").value("DESS Capability  Presentation"))
					.andExpect(jsonPath("$[1].createdModifiedBy").value("278648"))
					.andExpect(jsonPath("$[1].documentsAttached").value("YES"))
					.andExpect(jsonPath("$[1].primaryOwner").value("759726"))
					.andExpect(jsonPath("$[1].country").value("USA"))
					.andExpect(jsonPath("$[1].customerId").value("CUS545"))
					.andExpect(jsonPath("$[1].partnerId").value("PAT5"))
					.andDo(print()).andReturn();
					
		}
		
		@Test
		public void Test1ConnectByDate() throws Exception
		{
			mockMvc1.perform(get("/connect/date?from=20012015&to=30012015&owner=PRIMARY&fields=connectId,"
					+ "connectCategory,connectName,createdModifiedBy,primaryOwner").header("Authorization","Basic YWFhOmJiYg==")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$[0].connectId").value("CNN2"))
					.andExpect(jsonPath("$[0].connectCategory").value("PARTNER"))
					.andExpect(jsonPath("$[0].connectName").value("DESS Capability Presentation"))
					.andExpect(jsonPath("$[0].createdModifiedBy").value("198054"))
					.andExpect(jsonPath("$[0].primaryOwner").value("541045"))
					.andDo(print())
					.andReturn();
		}
		
		@Test
		public void TestCreateConnect() throws Exception {

//			JsonFactory jfactory = new JsonFactory();
//			JSONParser parser = new JSONParser();
//			Object obj = parser
//					.parse(new FileReader(
//							"/Users/bnpp/destination/Destination/src/test/java/com/tcs/destination/createconnect.json"));
	//
//			JSONObject jsonObject = (JSONObject) obj;
			String requestJson = "{ \"connectCategory\":\"CUSTOMER\",\"connectName\":\"new connect 70\",\"documentsAttached\":\"no\",\"country\":\"India\",\"endDatetimeOfConnect\": 982336120000,\"startDatetimeOfConnect\": 982336120000,\"primaryOwner\": \"541045\",\"connectKeywords\": \"a197,b908\",\"customerId\": \"CUS541\",\"connectCustomerContactLinkTs\": [{\"contactT\": {\"contactId\": \"CONTACT2\"}}],\"connectOfferingLinkTs\": [{\"offeringMappingT\": {\"offering\": \"Analytics\"}}],\"connectSecondaryOwnerLinkTs\": [{\"secondaryOwner\": \"541045\"}],\"connectSubSpLinkTs\": [{\"subSpMappingT\": {\"subSp\": \"ABIM - Products\"}}],"
					+ "\"connectTcsAccountContactLinkTs\": [{\"contactT\":{\"contactId\": \"CONTACT1\"}}],"
					+ "\"notesTs\": [{\"notesUpdated\": \"Sample note9 set 2 1\"},{\"notesUpdated\": \"Sample note9 set 2 2\"}]}";
			//jsonObject.toString();
			

			UserDetails userDetails = userDetailsService.loadUserByUsername("aaa");
			Authentication authToken = new UsernamePasswordAuthenticationToken(
					userDetails, userDetails.getPassword(),
					userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authToken);

			this.mockMvc.perform(					
					post("/connect")
							.contentType(TestUtil.APPLICATION_JSON_UTF8)
							.content(requestJson)
							.header("Authorization", "Basic YWFhOmJiYg==")
							.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andDo(print()).andReturn();
		}
			
		@Test
		public void TestEditConnect() throws Exception {

//			JsonFactory jfactory = new JsonFactory();
//			JSONParser parser = new JSONParser();
//			Object obj = parser
//					.parse(new FileReader(
//							"/Users/bnpp/destination/Destination/src/test/java/com/tcs/destination/createconnect.json"));
	//
//			JSONObject jsonObject = (JSONObject) obj;
			String requestJson = "{\"connectCategory\":\"CUSTOMER\",\"connectId\":\"CNN1\",\"connectName\":\"new connect 70\",\"documentsAttached\":\"no\",\"country\":\"India\",\"endDatetimeOfConnect\": 982336120000,\"startDatetimeOfConnect\": 982336120000,\"primaryOwner\": \"541045\",\"connectKeywords\": \"a197,b908\", \"customerId\": \"CUS541\", \"connectCustomerContactLinkTs\": [{\"connectCustomerContactLinkId\":\"CCC31\",\"contactT\": { \"contactId\": \"CONTACT2\"}}], \"connectOfferingLinkTs\": [{\"connectOfferingLinkId\":\"COF23\",\"offeringMappingT\": {\"offering\": \"BPaaS - Cloud Payment\"} }],\"connectSecondaryOwnerLinkTs\": [{\"connectSecondaryOwnerLinkId\":\"CSO20\",\"secondaryOwner\": \"541045\"}],\"connectSubSpLinkTs\": [{ \"connectSubSpLinkId\":\"CSs9\",\"subSpMappingT\": { \"subSp\": \"Mobility - Services\" }},{\"subSpMappingT\":  {\"subSp\": \"Cloud\"}}],\"connectTcsAccountContactLinkTs\": [ {\"connect_tcs_account_contact_link_id\":\"CTC11\",\"contactT\": {\"contactId\": \"CONTACT1\" }}], \"taskTs\": [{\"taskId\":\"TASK1\",\"createdModifiedBy\":\"541046\",\"createdModifiedDatetime\":982336120000,\"documentsAttached\":\"No\",\"taskStatus\":\"OPEN\",\"taskDescription\":\"Description\",\"targetDateForCompletion\":982336120000,\"taskOwner\":\"541046\",\"entityReference\":\"CUSTOMER\",\"opportunityId\":\"OPP1\"}],\"connectOpportunityLinkIdTs\": [{ \"connectOpportunityLinkId\":\"CNO1\",\"opportunityT\": {\"opportunityId\": \"OPP1\"}}],\"notesTs\": [{\"notesUpdated\": \"Sample note9 set 3 1 update\"},{ \"notesUpdated\": \"Sample note9 set 3 2 update\" }]}";
			//jsonObject.toString();
			

			UserDetails userDetails = userDetailsService.loadUserByUsername("aaa");
			Authentication authToken = new UsernamePasswordAuthenticationToken(
					userDetails, userDetails.getPassword(),
					userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authToken);

			this.mockMvc.perform(					
					put("/connect")
							.contentType(TestUtil.APPLICATION_JSON_UTF8)
							.content(requestJson)
							.header("Authorization", "Basic YWFhOmJiYg==")
							.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andDo(print()).andReturn();
		}
}
