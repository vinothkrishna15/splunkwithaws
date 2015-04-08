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

import java.io.File;
import java.io.FileReader;
import java.util.List;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

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
@ContextConfiguration({ "classpath:app-context.xml" })
@WebAppConfiguration
@ComponentScan(basePackageClasses = UserRepositoryUserDetailsService.class)
public class ConnectControllerTest {

	@Autowired
	WebApplicationContext ctx;

	@Autowired
	ConnectService connectService;

	@Autowired
	FilterChainProxy springSecurityFilterChain;

	@Autowired
	UserRepositoryUserDetailsService userDetailsService;

	MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ConnectController())
			.build();
	MockMvc mockMvc1 = MockMvcBuilders.standaloneSetup(new ConnectController())
			.build();

	@Before
	public void setup() {

		mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
		mockMvc1 = MockMvcBuilders.webAppContextSetup(ctx)
				.addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void Test1ConnectById() throws Exception {
		mockMvc.perform(
				get(
						"/connect/CNN3?fields=connectId,connectCategory,connectName")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.connectId").value("CNN3"))
				.andExpect(jsonPath("$.connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$.connectName").value("Cloud Connect"))
				.andDo(print()).andReturn();
	}

	@Test
	public void Test2ConnectById() throws Exception {
		mockMvc.perform(
				get("/connect/CNN1000").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void Test1ConnectByName() throws Exception {
		mockMvc.perform(
				get(
						"/connect?nameWith=Cloud&fields=ConnectT,connectId,connectCategory,connectName")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].connectId").value("CNN3"))
				.andExpect(jsonPath("$[0].connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$[0].connectName").value("Cloud Connect"))
				.andDo(print()).andReturn();
	}

	@Test
	public void Test2ConnectByName() throws Exception {
		mockMvc.perform(
				get("/connect?nameWith=ABCD")
						.accept(MediaType.APPLICATION_JSON)).andExpect(
				status().isNotFound());
	}

	@Test
	public void TestConnectByDateCustId() throws Exception {
		mockMvc1.perform(
				get(
						"/connect/date?from=01022015&to=20022015&customerId=CUS542&fields=connectId,"
								+ "connectCategory,connectName,createdModifiedBy,documentsAttached,primaryOwner,partnerId,country")
						.header("Authorization", "Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].connectId").value("CNN3"))
				.andExpect(jsonPath("$[0].connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$[0].connectName").value("Cloud Connect"))
				.andExpect(jsonPath("$[0].createdModifiedBy").value("541045"))
				.andExpect(jsonPath("$[0].documentsAttached").value("no"))
				.andExpect(jsonPath("$[0].primaryOwner").value("833389"))
				.andExpect(jsonPath("$[0].country").value("USA"))
				.andDo(print()).andReturn();
	}

	@Test
	public void TestConnectByDatePartId() throws Exception {
		mockMvc1.perform(
				get(
						"/connect/date?from=01022015&to=20022015&partnerId=PAT5&fields=connectId,connectCategory,"
								+ "connectName,createdModifiedBy,documentsAttached,primaryOwner,customerId,partnerId,country")
						.header("Authorization", "Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].connectId").value("CNN6"))
				.andExpect(jsonPath("$[0].connectCategory").value("PARTNER"))
				.andExpect(
						jsonPath("$[0].connectName").value(
								"DESS Capability  Presentation"))
				.andExpect(jsonPath("$[0].createdModifiedBy").value("541045"))
				.andExpect(jsonPath("$[0].documentsAttached").value("NO"))
				.andExpect(jsonPath("$[0].primaryOwner").value("759726"))
				.andExpect(jsonPath("$[0].country").value("USA"))
				.andExpect(jsonPath("$[0].partnerId").value("PAT5"))
				.andDo(print()).andReturn();

	}

	@Test
	public void Test1ConnectByDate() throws Exception {
		mockMvc1.perform(
				get(
						"/connect/date?from=20012015&to=30012015&owner=PRIMARY&fields=connectId,"
								+ "connectCategory,connectName,createdModifiedBy,primaryOwner")
						.header("Authorization", "Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].connectId").value("CNN2"))
				.andExpect(jsonPath("$[0].connectCategory").value("PARTNER"))
				.andExpect(
						jsonPath("$[0].connectName").value(
								"DESS Capability Presentation"))
				.andExpect(jsonPath("$[0].createdModifiedBy").value("465897"))
				.andExpect(jsonPath("$[0].primaryOwner").value("541045"))
				.andDo(print()).andReturn();
	}
	
	@Test
	public void TestConnectByOwnerWeekStartEndDate() throws Exception {
		mockMvc1.perform(
				get("/connect/date?from=01022015&to=20022015&owner=PRIMARY&userId=833389&customerId=CUS542&weekStartDate=01022015&"
						+ "weekEndDate=07022015&fields=connectTs,connectId,"
						+ "connectCategory,connectName,createdModifiedBy,documentsAttached,primaryOwner,customerId,country")
						.header("Authorization", "Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.connectTs[0].connectId").value("CNN3"))
				.andExpect(jsonPath("$.connectTs[0].connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$.connectTs[0].connectName").value("Cloud Connect"))
				.andExpect(jsonPath("$.connectTs[0].createdModifiedBy").value("541045"))
				.andExpect(jsonPath("$.connectTs[0].documentsAttached").value("no"))
				.andExpect(jsonPath("$.connectTs[0].primaryOwner").value("833389"))
				.andExpect(jsonPath("$.connectTs[0].country").value("USA"))
				.andDo(print()).andReturn();
	}
	
	@Test
	public void Test1ConnectByOwnerWeekStartEndDate() throws Exception {
		mockMvc1.perform(
				get("/connect/date?from=01022015&to=20022015&owner=PRIMARY&userId=833389&customerId=CUS542&weekStartDate=01022015&"
						+ "weekEndDate=07022015&fields=connectTs,connectId,"
						+ "connectCategory,connectName,createdModifiedBy,documentsAttached,primaryOwner,customerId,country")
						.header("Authorization", "Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.connectTs[0].connectId").value("CNN3"))
				.andExpect(jsonPath("$.connectTs[0].connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$.connectTs[0].connectName").value("Cloud Connect"))
				.andExpect(jsonPath("$.connectTs[0].createdModifiedBy").value("541045"))
				.andExpect(jsonPath("$.connectTs[0].documentsAttached").value("no"))
				.andExpect(jsonPath("$.connectTs[0].primaryOwner").value("833389"))
				.andExpect(jsonPath("$.connectTs[0].country").value("USA"))
				.andDo(print()).andReturn();
	}
	
	@Test
	public void TestConnectByWeekStartEndDate() throws Exception {
		mockMvc1.perform(
				get("/connect/date?from=01022015&to=20022015&userId=833389&customerId=CUS542&weekStartDate=01022015&"
						+ "weekEndDate=07022015&fields=connectTs,connectId,"
						+ "connectCategory,connectName,createdModifiedBy,documentsAttached,primaryOwner,customerId,country")
						.header("Authorization", "Basic YWFhOmJiYg==").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.connectTs[0].connectId").value("CNN3"))
				.andExpect(jsonPath("$.connectTs[0].connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$.connectTs[0].connectName").value("Cloud Connect"))
				.andExpect(jsonPath("$.connectTs[0].createdModifiedBy").value("541045"))
				.andExpect(jsonPath("$.connectTs[0].documentsAttached").value("no"))
				.andExpect(jsonPath("$.connectTs[0].primaryOwner").value("833389"))
				.andExpect(jsonPath("$.connectTs[0].country").value("USA"))
				.andDo(print()).andReturn();
	}
	
	@Test
	public void TestConnectByOwnerMonthStartEndDate() throws Exception {
		mockMvc1.perform(
				get("/connect/date?from=01022015&to=20022015&owner=PRIMARY&userId=833389&customerId=CUS542&monthStartDate=01022015&"
						+ "monthEndDate=28022015&fields=connectTs,connectId,"
						+ "connectCategory,connectName,createdModifiedBy,documentsAttached,primaryOwner,customerId,country")
						.header("Authorization", "Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.connectTs[0].connectId").value("CNN3"))
				.andExpect(jsonPath("$.connectTs[0].connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$.connectTs[0].connectName").value("Cloud Connect"))
				.andExpect(jsonPath("$.connectTs[0].createdModifiedBy").value("541045"))
				.andExpect(jsonPath("$.connectTs[0].documentsAttached").value("no"))
				.andExpect(jsonPath("$.connectTs[0].primaryOwner").value("833389"))
				.andExpect(jsonPath("$.connectTs[0].country").value("USA"))
				.andDo(print()).andReturn();
	}


	@Test
	public void Test1ConnectByOwnerMonthStartEndDate() throws Exception {
		mockMvc1.perform(
				get("/connect/date?from=01022015&to=20032015&owner=PRIMARY&userId=833389&customerId=CUS542&monthStartDate=01022015&monthEndDate=28022015&fields=connectTs,connectId,connectCategory,connectName,createdModifiedBy,documentsAttached,primaryOwner,customerId,country")
						.header("Authorization", "Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.connectTs[0].connectId").value("CNN3"))
				.andExpect(jsonPath("$.connectTs[0].connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$.connectTs[0].connectName").value("Cloud Connect"))
				.andExpect(jsonPath("$.connectTs[0].createdModifiedBy").value("541045"))
				.andExpect(jsonPath("$.connectTs[0].documentsAttached").value("no"))
				.andExpect(jsonPath("$.connectTs[0].primaryOwner").value("833389"))
				.andExpect(jsonPath("$.connectTs[0].country").value("USA"))
				.andDo(print()).andReturn();
	}


	@Test
	public void TestConnectByMonthStartEndDate() throws Exception {
		mockMvc1.perform(
				get("/connect/date?from=01022015&to=20022015&userId=833389&customerId=CUS542&monthStartDate=01022015&"
						+ "monthEndDate=28022015&fields=connectTs,connectId,"
						+ "connectCategory,connectName,createdModifiedBy,documentsAttached,primaryOwner,customerId,country")
						.header("Authorization", "Basic YWFhOmJiYg==").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.connectTs[0].connectId").value("CNN3"))
				.andExpect(jsonPath("$.connectTs[0].connectCategory").value("CUSTOMER"))
				.andExpect(jsonPath("$.connectTs[0].connectName").value("Cloud Connect"))
				.andExpect(jsonPath("$.connectTs[0].createdModifiedBy").value("541045"))
				.andExpect(jsonPath("$.connectTs[0].documentsAttached").value("no"))
				.andExpect(jsonPath("$.connectTs[0].primaryOwner").value("833389"))
				.andExpect(jsonPath("$.connectTs[0].country").value("USA"))
				.andDo(print()).andReturn();
	}

	
	
	@Test
	public void TestCreateConnect() throws Exception {
		String requestJson = TestUtil.getJsonString(TestConstants.requestJsonCreateConnectLoc);
		TestUtil.setAuthToken(userDetailsService);
		this.mockMvc.perform(post("/connect")
								.contentType(TestUtil.APPLICATION_JSON_UTF8)
								.content(requestJson)
								.header("Authorization", "Basic YWFhOmJiYg==")
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestCreateConnectError() throws Exception {
		String requestJson = TestUtil.getJsonString(TestConstants.requestJsonCreateConnectError);
		TestUtil.setAuthToken(userDetailsService);
		this.mockMvc.perform(post("/connect")
								.contentType(TestUtil.APPLICATION_JSON_UTF8)
								.content(requestJson)
								.header("Authorization", "Basic YWFhOmJiYg==")
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError()).andDo(print()).andReturn();
	}


	@Test
	public void TestEditConnect() throws Exception {
		String requestJson = TestUtil.getJsonString(TestConstants.requestJsonEditConnectLoc);
		TestUtil.setAuthToken(userDetailsService);
		this.mockMvc
				.perform(put("/connect")
								.contentType(TestUtil.APPLICATION_JSON_UTF8)
								.content(requestJson)
								.header("Authorization", "Basic YWFhOmJiYg==")
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestEditConnectInternalServerError() throws Exception {
		String requestJson = TestUtil.getJsonString(TestConstants.requestJsonEditConnectError);
		TestUtil.setAuthToken(userDetailsService);
		this.mockMvc
				.perform(put("/connect")
								.contentType(TestUtil.APPLICATION_JSON_UTF8)
								.content(requestJson)
								.header("Authorization", "Basic YWFhOmJiYg==")
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError()).andDo(print()).andReturn();
	}
	
}
