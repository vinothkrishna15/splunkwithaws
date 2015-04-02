package com.tcs.destination;

import java.lang.reflect.Member;
import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.DestinationApplication;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.controller.PartnerController;
import com.tcs.destination.controller.UserDetailsController;
import com.tcs.destination.data.repository.PartnerRepository;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:app-context.xml")
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@WebAppConfiguration
public class PartnerControllerTest {

	
	@Autowired
	PartnerRepository partnerRepository;
	
	@Autowired
	WebApplicationContext wvc6;
	
	MockMvc mockMvcuser	=MockMvcBuilders.standaloneSetup(new PartnerController()).build();
	
	@Before
	public void setUp() throws Exception 
	{
		mockMvcuser=MockMvcBuilders.webAppContextSetup(wvc6).build();
	}

	@Test
	public void TestPartnerById() throws Exception
	{
		mockMvcuser.perform(get("/partner/PAT4?fields=partnerId,corporateHqAddress,documentsAttached,partnerName").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.partnerId").value("PAT4"))
		.andExpect(jsonPath("$.corporateHqAddress").value(""))
		.andExpect(jsonPath("$.documentsAttached").value("YES"))
		.andExpect(jsonPath("$.partnerName").value("Microsoft"))
		.andDo(print())
		.andReturn();
	}
	
	@Test
	public void TestPartnerByIdNotFound() throws Exception
	{
		mockMvcuser.perform(get("/partner/PAT4000").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
}