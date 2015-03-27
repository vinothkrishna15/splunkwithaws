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
			.andExpect(jsonPath("$.createdModifiedBy").value("887053"))
			.andExpect(jsonPath("$.documentsAttached").value("No"))
			
			.andExpect(jsonPath("$.taskOwner").value("833389"))
			.andExpect(jsonPath("$.taskOwnerName").value("Parvez Patel"))
			.andExpect(jsonPath("$.connectId").value("CNN1"))
			.andExpect(jsonPath("$.opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
		
	@Test
	public void TestTaskByName() throws Exception
	{
		mockMvc.perform(get("/task?nameWith=Description&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS1"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("887053"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("833389"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("Parvez Patel"))
			.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskByConnectId() throws Exception
	{
		mockMvc.perform(get("/task/findByConnect?id=CNN1&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS1"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("887053"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("833389"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("Parvez Patel"))
			.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskByOpportunityId() throws Exception
	{
		mockMvc.perform(get("/task/findByOpportunity?id=OPP1&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS1"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("887053"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("833389"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("Parvez Patel"))
			.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskByOwnerId() throws Exception
	{   
		
		mockMvc.perform(get("/task/findByOwner?id=833389&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS1"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("887053"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("833389"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("Parvez Patel"))
			.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskByUserId() throws Exception
	{
		mockMvc.perform(get("/task/findAssigned?id=887053&fields=taskId,createdModifiedBy,documentsAttached,"
				+ "taskOwner,taskOwnerName,connectId,opportunityId").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$[0].taskId").value("TAS1"))
			.andExpect(jsonPath("$[0].createdModifiedBy").value("887053"))
			.andExpect(jsonPath("$[0].documentsAttached").value("No"))
			.andExpect(jsonPath("$[0].taskOwner").value("833389"))
			.andExpect(jsonPath("$[0].taskOwnerName").value("Parvez Patel"))
			.andExpect(jsonPath("$[0].connectId").value("CNN1"))
			.andExpect(jsonPath("$[0].opportunityId").value("OPP1"))
			.andDo(print()).andReturn();
	}
	
	@Test
	public void TestTaskForCreate() throws Exception{
		String requestJson="{\"collaborationPreference\": \"Restricted\", \"createdModifiedBy\": \"887054\", \"createdModifiedDatetime\": 1426617000000,"
   +" \"documentsAttached\": \"No\", \"entityReference\": \"Opportunity\", \"targetDateForCompletion\": 1426617000000, \"taskDescription\": \"Task for Test45\","
   +" \"taskStatus\": \"Open\", \"collaborationCommentTs\": [ {\"commentType\": \"Task\", \"comments\": \"Comments for Task45\","
     +"       \"documentsAttached\": \"No\", \"entityType\": \"Task\", \"updatedDatetime\": 1426849920000,\"userId\": \"541045\","
       +"     \"documentRepositoryTs\": []}],\"documentRepositoryTs\": [],    \"notesTs\": [{\"createdDatetime\": 1426763160000,\"entityType\": \"Task\","
         +"   \"notesUpdated\": \"Notes for Task45\",\"connectT\": null,\"customerMasterT\": null,\"opportunityT\": null,\"partnerMasterT\": null,"
	   +" \"userT\": {\"userId\": \"541045\" } } ], \"taskBdmsTaggedLinkTs\": [ { \"createdModifiedBy\": \"541045\",\"createdModifiedDatetime\": 1426852080000,\"userT\": { \"userId\":\"833389\" } }  ],  \"userNotificationsTs\": [{\"comments\": \"Comments for User Notifications45\",\"entityType\": \"Task\",\"read\": \"TSK\", \"updatedDatetime\": 1426851120000,\"userT\": \"833389\"}  ], \"taskOwner\": \"541045\",\"connectId\": \"CNN8\", \"opportunityId\": \"OPP2\"}";
		UserDetails userDetails = userDetailsService.loadUserByUsername("aaa");
		Authentication authToken = new UsernamePasswordAuthenticationToken(
				userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);
		mockMvc.perform(post("/task").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	
	@Test
	public void TestEditConnect() throws Exception {

		String requestJson ="{    \"taskId\": \"TAS56\",    \"collaborationPreference\": \"Restricted\",    \"createdModifiedBy\": \"887053\",    \"createdModifiedDatetime\": 1426617000000,    \"documentsAttached\": \"No\",    \"entityReference\": \"Opportunity\",    \"targetDateForCompletion\": \"2015-03-18\",    \"taskDescription\": \"Task for Test45\",    \"taskStatus\": \"Hold\",    \"collaborationCommentTs\": [        {            \"commentId\": \"COC9\",            \"commentType\": \"Task\",            \"comments\": \"Comments updated for Task45\",            \"documentsAttached\": \"No\",            \"entityType\": \"Task\",            \"updatedDatetime\": 1426849920000,            \"connectId\": null,            \"connectT\": null,            \"opportunityId\": null,            \"opportunityT\": null,            \"taskId\": \"TAS56\",            \"taskT\": \"TAS56\",            \"userId\": \"833389\",            \"userT\": {                \"userId\": \"833389\" 	    },            \"documentRepositoryTs\": []        }    ],    \"documentRepositoryTs\": [],    \"notesTs\": [        {            \"noteId\": \"NOT12\",            \"createdDatetime\": 1426763160000,            \"entityType\": \"Task\",            \"notesUpdated\": \"Notes updated for Task45\",            \"connectT\": null,            \"connectId\": null,            \"customerMasterT\": null,            \"opportunityT\": null,            \"partnerMasterT\": null,            \"taskT\": \"TAS56\",            \"userT\": \"833389\"        }    ],    \"taskBdmsTaggedLinkTs\": [        {            \"createdModifiedBy\": \"833389\",            \"createdModifiedDatetime\": 1426852080000,            \"taskT\": \"TAS56\",            \"userT\": {                \"userId\": \"541045\"            }        }    ],    \"userNotificationsTs\": [        {            \"userNotificationId\": \"USN7\",            \"comments\": \"Comments updated for User Notifications45\",            \"entityType\": \"Task\",            \"read\": \"TSK\",            \"updatedDatetime\": 1426851120000,            \"connectT\": null,            \"opportunityT\": null,            \"taskT\": \"TAS56\",            \"userT\": {                \"userId\": \"554654\"            }        }    ],    \"taskOwner\": \"541045\",    \"taskOwnerName\": \"User200\",    \"connectId\": null,    \"opportunityId\": \"OPP1\",    \"taskBdmsTaggedLinkDeletionList\": [        {            \"taskBdmsTaggedLinkId\": \"TBT28\",            \"createdModifiedBy\": \"541045\",            \"createdModifiedDatetime\": 1426852080000,            \"taskT\": \"TAS56\",            \"userT\": \"554654\"        }    ]}";

		UserDetails userDetails = userDetailsService.loadUserByUsername("aaa");
		Authentication authToken = new UsernamePasswordAuthenticationToken(
				userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);

		this.mockMvc.perform(					
				put("/task")
						.contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}
	
}