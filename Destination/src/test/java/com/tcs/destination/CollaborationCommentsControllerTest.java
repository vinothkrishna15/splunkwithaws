package com.tcs.destination;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
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

import com.tcs.destination.controller.CollaborationCommentsController;
import com.tcs.destination.controller.PartnerController;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.PartnerRepository;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:app-context.xml")
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@WebAppConfiguration
public class CollaborationCommentsControllerTest {

	@Autowired
	CollaborationCommentsRepository collaborationCommentsRepository;
	
	@Autowired
	WebApplicationContext wvc;
	
	MockMvc mockMvc	=MockMvcBuilders.standaloneSetup(new CollaborationCommentsController()).build();
	
	@Before
	public void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(wvc).build();
	}

	@Test
	public void test() throws Exception{
		String requestJson="{ \"commentType\": \"USER\", \"comments\": \"USER Comments valid\",\"documentsAttached\": \"YES\",\"entityType\": \"CUSTOMER\",\"updatedDatetime\": 982336120000,\"connectId\": \"CNN1\",\"opportunityId\":\"OPP2\",\"taskId\":\"TAS3\",\"userId\":\"886301\"}";
		mockMvc.perform(post("/comments").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(requestJson)
						.header("Authorization", "Basic YWFhOmJiYg==")
						.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andDo(print()).andReturn();
	}

}
