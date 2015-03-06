package com.tcs.destination;
import java.lang.reflect.Member;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.DestinationApplication;
import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.controller.FrequentlySearchedController;
import com.tcs.destination.controller.UserDetailsController;
import com.tcs.destination.service.FrequentlySearchedCustPartService;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:app-context.xml")
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@WebAppConfiguration
public class FrequentlySearchedControllerTest {

	
	@Autowired
	FrequentlySearchedCustPartService frequentService;
	
	@Autowired
	WebApplicationContext wvc2;
	
	MockMvc mockMvcuser	=MockMvcBuilders.standaloneSetup(new FrequentlySearchedController()).build();
	
	@Before
	public void setUp() throws Exception 
	{
		mockMvcuser=MockMvcBuilders.webAppContextSetup(wvc2).build();
	}

	@Test
	public void test() throws Exception {
		mockMvcuser.perform(get("/frequent").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id").value("CUS8"))
		.andExpect(jsonPath("$[0].name").value("ALCATEL-LUCENT INTERNATIONAL"))
		.andExpect(jsonPath("$[0].groupCustomerName").value("ALCATEL-LUCENT"))
		.andExpect(jsonPath("$[0].geographyMappingT.geography").value("Europe"))
		.andExpect(jsonPath("$[0].geographyMappingT.active").value("Y"))
		.andExpect(jsonPath("$[0].geographyMappingT.displayGeography").value("EU&UK"))
		.andExpect(jsonPath("$[1].id").value("PAT4"))
		.andExpect(jsonPath("$[1].name").value("Microsoft"))
		.andExpect(jsonPath("$[1].geographyMappingT.geography").value("Europe"))
		.andExpect(jsonPath("$[1].geographyMappingT.active").value("Y"))
		.andExpect(jsonPath("$[1].geographyMappingT.displayGeography").value("EU&UK"))
	 	.andExpect(jsonPath("$[1].logo").value(""))
		.andDo(print())
		.andReturn();

			
		List<CustPartResultCard> cus= frequentService.frequentCustPart();
		assertNotNull(cus);
		assertEquals("CUS8",cus.get(0).getId());
		assertNull(cus.get(0).getLogo());
		assertEquals(cus.get(0).getCreatedModifiedDatetime().toString(),"2015-02-25 09:40:52.182842");
		assertNull(cus.get(1).getGroupCustomerName());
		assertEquals(cus.get(1).getCreatedModifiedDatetime().toString(),"2015-02-25 09:40:52.182842");
		
		
		
		
	}
	public static class TestUtil 
	{
	    public final static MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
		MediaType.APPLICATION_JSON.getSubtype(),                      
	    Charset.forName("utf8"));
	}

}
