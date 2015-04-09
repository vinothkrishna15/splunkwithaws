package com.tcs.destination;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.tcs.destination.controller.FollowedController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class FollowedControllerTest {

	@Autowired 
	WebApplicationContext ctx;
	
	MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new FollowedController()).build(); 
	 
	@Before
	public void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
	}

	@Test
	public void TestFollowByUserIdEntityTypeConnect() throws Exception {
		mockMvc.perform(get("/follow?userId=833389&entityType=CONNECT&fields=userTaggedFollowedId,createdModifiedBy,entityType,connectId,userId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].userTaggedFollowedId").value("UTF1"))
		.andExpect(jsonPath("$[0].connectId").value("CNN1"))
		.andExpect(jsonPath("$[0].createdModifiedBy").value("541045"))
		.andExpect(jsonPath("$[0].entityType").value("CONNECT"))
		.andExpect(jsonPath("$[0].userId").value("833389"))
		.andDo(print())
		.andReturn();
	}

	@Test
	public void TestFollowByUserIdEntityTypeTask() throws Exception {
		mockMvc.perform(get("/follow?userId=886301&entityType=TASK&fields=userTaggedFollowedId,createdModifiedBy,entityType,taskId,userId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].userTaggedFollowedId").value("UTF5"))
		.andExpect(jsonPath("$[0].taskId").value("TAS2"))
		.andExpect(jsonPath("$[0].createdModifiedBy").value("541045"))
		.andExpect(jsonPath("$[0].entityType").value("TASK"))
		.andExpect(jsonPath("$[0].userId").value("886301"))
		.andExpect(jsonPath("$[1].userTaggedFollowedId").value("UTF6"))
		.andExpect(jsonPath("$[2].userTaggedFollowedId").value("UTF7"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void TestFollowByUserIdEntityTypeOpportunity() throws Exception {
		mockMvc.perform(get("/follow?userId=465897&entityType=OPPORTUNITY&fields=userTaggedFollowedId,createdModifiedBy,entityType,opportunityId,userId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$[0].userTaggedFollowedId").value("UTF9"))
		.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
		.andExpect(jsonPath("$[0].createdModifiedBy").value("541045"))
		.andExpect(jsonPath("$[0].entityType").value("OPPORTUNITY"))
		.andExpect(jsonPath("$[0].userId").value("465897"))
		.andExpect(jsonPath("$[1].userTaggedFollowedId").value("UTF10"))
		.andDo(print())
		.andReturn();
	}
	
	
	@Test
	public void TestFollowByUserIdEntityTypeConnectNotFound() throws Exception {
		mockMvc.perform(get("/follow?userId=887053&entityType=CONNECT").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.status").value("Failed"))
		.andExpect(jsonPath("$.description").value("No Relevent Data Found in the database"))
		.andDo(print()).andReturn();
	}

	@Test
	public void TestFollowByUserIdEntityTypeTaskNotFound() throws Exception {
		mockMvc.perform(get("/follow?userId=887053&entityType=TASK").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.status").value("Failed"))
		.andExpect(jsonPath("$.description").value("No Relevent Data Found in the database"))
		.andDo(print()).andReturn();
	}
	
	@Test
	public void TestFollowByUserIdEntityTypeOpportunityNotFound() throws Exception {
		mockMvc.perform(get("/follow?userId=887053&entityType=OPPORTUNITY").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.status").value("Failed"))
		.andExpect(jsonPath("$.description").value("No Relevent Data Found in the database"))
		.andDo(print()).andReturn();
	}
	
	@Test
	public void TestFollowByUserIdEntityTypeNotFound() throws Exception {
		mockMvc.perform(get("/follow?userId=833389&entityType=ABCDEF").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.status").value("Failed"))
		.andExpect(jsonPath("$.description").value("No such Entity type exists. Please ensure your entity type"))
		.andDo(print()).andReturn();
	}

	@Test
	public void Test1FollowByUserIdEntityTypeNotToFollow() throws Exception {
		mockMvc.perform(get("/follow?userId=833389&entityType=CUSTOMER").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.status").value("Failed"))
		.andExpect(jsonPath("$.description").value("You cannot follow CUSTOMER"))
		.andDo(print()).andReturn();
	}
	
	@Test
	public void Test2FollowByUserIdEntityTypeNotToFollow() throws Exception {
		mockMvc.perform(get("/follow?userId=833389&entityType=PARTNER").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.status").value("Failed"))
		.andExpect(jsonPath("$.description").value("You cannot follow PARTNER"))
		.andDo(print()).andReturn();
	}
	
	@Test
	public void Test3FollowByUserIdEntityTypeNotToFollow() throws Exception {
		mockMvc.perform(get("/follow?userId=833389&entityType=DOCUMENT").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.status").value("Failed"))
		.andExpect(jsonPath("$.description").value("You cannot follow DOCUMENT"))
		.andDo(print()).andReturn();
	}
	
	@Test
	public void TestToAddFollowEntityTypeConect() throws Exception{
		String requestJson="{\"connectId\":\"CNN5\",\"createdModifiedBy\":\"886301\",\"entityType\":\"CONNECT\",\"userId\":\"465897\" }";
		mockMvc.perform(post("/follow").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestToAddFollowEntityTypeOpportunity() throws Exception{
		String requestJson="{  \"createdModifiedBy\": \"833389\", \"entityType\": \"OPPORTUNITY\",\"opportunityId\": \"OPP12\", \"userId\": \"541045\"}";
		mockMvc.perform(post("/follow").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestToAddFollowEntityTypeTask() throws Exception{
		String requestJson="{ \"createdModifiedBy\": \"541045\",\"entityType\": \"TASK\",\"taskId\": \"TAS5\", \"userId\": \"886301\"}";
		mockMvc.perform(post("/follow").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}
}