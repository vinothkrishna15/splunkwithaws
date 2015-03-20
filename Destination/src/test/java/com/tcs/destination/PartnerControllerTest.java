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
	public void test() throws Exception
	{
		mockMvcuser.perform(get("/partner/PAT4?fields=partnerId,corporateHqAddress").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.partnerId").value("PAT4"))
		.andExpect(jsonPath("$.corporateHqAddress").value(""))
		.andDo(print())
		.andReturn()
		;
		
//		PartnerMasterT pat=partnerRepository.findOne("PAT4");
//		assertNotNull(pat);
//		assertEquals(pat.getPartnerName(),"Microsoft");
//		assertEquals(pat.getDocumentsAttached(),"YES");
//		assertEquals(pat.getConnectTs().get(0).getConnectId(),"CNN2");
//		assertEquals(pat.getConnectTs().get(0).getConnectCategory(),"PARTNER");
//		assertEquals(pat.getConnectTs().get(0).getConnectName(),"DESS Capability Presentation");
////		assertEquals(pat.getConnectTs().get(0).getConnectOpportunityLinkId(),"CNO5");
//		assertEquals(pat.getConnectTs().get(0).getDocumentsAttached(),"Y");
//		assertEquals(pat.getContactTs().get(0).getContactId(),"CON8");
//		assertEquals(pat.getContactTs().get(0).getContactCategory(),"Partner");
//		assertEquals(pat.getContactTs().get(0).getContactName(),"Satya Nadella");
//		assertEquals(pat.getContactTs().get(0).getContactType(),"External");
//		assertEquals(pat.getContactTs().get(0).getEmployeeNumber(),"018323");
//		assertEquals(pat.getConnectTs().get(0).getCreatedModifiedDatetime().toString(),"2015-01-20 15:40:35.0");
//	//	assertEquals(pat.getConnectTs().get(0).getDateOfConnect().toString(),"2015-01-20 15:30:45.0");
//		assertEquals(pat.getConnectTs().get(0).getCreatedModifiedBy(),"198054");
//    	assertEquals(pat.getContactTs().get(0).getCreatedModifiedBy(),"278648");
//    	
//    	// for negative inputs
//    	PartnerMasterT patneg=partnerRepository.findOne("PAT23");
//		assertNull(patneg);
    	
	}
	
}
