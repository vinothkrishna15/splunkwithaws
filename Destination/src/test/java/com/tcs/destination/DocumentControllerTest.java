package com.tcs.destination;

import static org.junit.Assert.*;
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

import com.tcs.destination.controller.DocumentController;
import com.tcs.destination.controller.OpportunityController;
import com.tcs.destination.service.DocumentService;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:app-context.xml")
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@WebAppConfiguration
public class DocumentControllerTest {

	@Autowired
	DocumentService documentService;
	
	@Autowired
	WebApplicationContext wvc;
	
	MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new DocumentController()).build();
	
	@Before
	public void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(wvc).build();
	}

	@Test
	public void toTestGetDocumentUsingId() throws Exception {
		mockMvc.perform(get("/document/DOC1?fields=documentId,connectT,connectId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.documentId").value("DOC1"))
		.andExpect(jsonPath("$.connectT.connectId").value("CNN1"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void toTestNegativeInputs() throws Exception {
		mockMvc.perform(get("/docuerment/DCO1?fields=documentIed,connecetT,conneectId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}

}
