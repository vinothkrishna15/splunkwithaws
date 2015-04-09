package com.tcs.destination;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.bean.TaskT;
import com.tcs.destination.controller.TaskController;
import com.tcs.destination.controller.UserRepositoryUserDetailsService;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.service.TaskService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
@ComponentScan(basePackageClasses=UserRepositoryUserDetailsService.class)
public class TaskControllerTest {

	 @Autowired 
	 WebApplicationContext ctx;
	 
	 @Autowired
	 TaskRepository taskRepository;
	 
	 @Autowired 
	 TaskService taskService;
	 @Autowired
	 UserRepositoryUserDetailsService userDetailsService;
	 
	 @Autowired
	FilterChainProxy springSecurityFilterChain;

			 	 
	 MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new TaskController()).build();
		

	@Before
	public void setup()
	{
		mockMvc=MockMvcBuilders.webAppContextSetup(ctx).build();
			
		}
		
				
	@Test
	public void TestTaskById() throws Exception
	{
		mockMvc.perform(get("/task/TAS1?fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.taskId").value("TAS1"))
			.andExpect(jsonPath("$.createdModifiedBy").value("541045"))
			.andExpect(jsonPath("$.documentsAttached").value("No"))
			
			.andExpect(jsonPath("$.taskOwner").value("833389"))
			.andExpect(jsonPath("$.taskOwnerName").value("Parvez Patel"))
			//.andExpect(jsonPath("$.connectId").value("CNN1"))
			.andExpect(jsonPath("$.opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
		
	@Test
	public void TestTaskByIdNotFound() throws Exception
	{
		mockMvc.perform(get("/task/TAS1000").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskByName() throws Exception
	{
		mockMvc.perform(get("/task?nameWith=Description&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS3"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("886301"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("833389"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("Parvez Patel"))
			//.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}

	@Test
	public void TestTaskByNameNotFound() throws Exception
	{
		mockMvc.perform(get("/task?nameWith=ABCD").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskByConnectId() throws Exception
	{
		mockMvc.perform(get("/task/findByConnect?id=CNN1&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS8"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("541045"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("541045"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("aaa"))
			.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskByConnectIdNotFound() throws Exception
	{
		mockMvc.perform(get("/task/findByConnect?id=CNN1000").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskByOpportunityId() throws Exception
	{
		mockMvc.perform(get("/task/findByOpportunity?id=OPP1&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS3"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("886301"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("833389"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("Parvez Patel"))
			//.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskByOpportunityIdNotFound() throws Exception
	{
		mockMvc.perform(get("/task/findByOpportunity?id=OPP1000").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andDo(print()).andReturn();
	}
	
	
	@Test
	public void TestTaskByOwnerId() throws Exception
	{   
		
		mockMvc.perform(get("/task/findByOwner?id=833389&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS3"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("886301"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("833389"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("Parvez Patel"))
			//.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
	

	@Test
	public void TestTaskByOwnerIdNotFound() throws Exception
	{   
		
		mockMvc.perform(get("/task/findByOwner?id=887053").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskByUserId() throws Exception
	{
		mockMvc.perform(get("/task/findAssigned?id=541045&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS1"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("541045"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("833389"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("Parvez Patel"))
			//.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
	
	
	
	@Test
	public void TestTaskByTargetDate() throws Exception
	{   
		
		mockMvc.perform(get("/task/findByTargetDate?id=833389&date=2015-03-18&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS3"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("886301"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("833389"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("Parvez Patel"))
			//.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestCreateTask() throws Exception {
		String requestJson = TestUtil.getJsonString(TestConstants.requestJsonCreateTaskLoc);
		TestUtil.setAuthToken(userDetailsService);
		this.mockMvc.perform(post("/task")
								.contentType(TestUtil.APPLICATION_JSON_UTF8)
								.content(requestJson)
								.header("Authorization", "Basic YWFhOmJiYg==")
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	@Test
	public void TestEditTask() throws Exception {
		String requestJson = TestUtil.getJsonString(TestConstants.requestJsonEditTaskLoc);
		TestUtil.setAuthToken(userDetailsService);
		this.mockMvc
				.perform(put("/task")
								.contentType(TestUtil.APPLICATION_JSON_UTF8)
								.content(requestJson)
								.header("Authorization", "Basic YWFhOmJiYg==")
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print()).andReturn();
	}
}