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
import com.tcs.destination.bean.FrequentlySearchedCustomerPartnerT;
import com.tcs.destination.controller.FrequentlySearchedController;
import com.tcs.destination.controller.UserDetailsController;


@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:app-context.xml")
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@WebAppConfiguration
public class FrequentlySearchedControllerTest {

	
	@Autowired
	FrequentlySearchedController frequentService;
	
	@Autowired
	WebApplicationContext wvc2;
	
	MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new 
			FrequentlySearchedController()).build();
	
	@Before
	public void setUp() throws Exception 
	{
		mockMvc=MockMvcBuilders.webAppContextSetup(wvc2).build();
	}

	@Test
	public void Test1FrequentSearchCustPart() throws Exception {
			mockMvc.perform(get("/frequent?entity=partner&fields=entity,partnerId,corporateHqAddress").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].entity.partnerId").value("PAT4"))
				.andExpect(jsonPath("$[0].entity.corporateHqAddress").value(""))
				.andExpect(jsonPath("$[1].entity.partnerId").value("PAT5"))
				.andExpect(jsonPath("$[1].entity.corporateHqAddress").value(""))
				.andExpect(jsonPath("$[2].entity.partnerId").value("PAT6"))
				.andExpect(jsonPath("$[2].entity.corporateHqAddress").value(""))
				.andDo(print())
				.andReturn();
	}
	
	
	@Test
	public void Test2FrequentSearchCustPart() throws Exception {
				mockMvc.perform(get("/frequent?entity=CUSTOMER&fields=count,entity,customerId,createdModifiedBy,"
						+ "createdModifiedDatetime,customerName,documentsAttached,groupCustomerName").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].count").value(2))
				.andExpect(jsonPath("$[0].entity.customerId").value("CUS546"))
				.andExpect(jsonPath("$[0].entity.createdModifiedBy").value("287694"))
				.andExpect(jsonPath("$[0].entity.customerName").value("ASDA Stores Limited"))
				.andExpect(jsonPath("$[0].entity.documentsAttached").value("YES"))
				.andExpect(jsonPath("$[0].entity.groupCustomerName").value("ASDA Stores Limited"))
				.andExpect(jsonPath("$[1].count").value(2))
				.andExpect(jsonPath("$[2].count").value(1))
				.andExpect(jsonPath("$[3].count").value(1))
				.andDo(print())
				.andReturn();
			}
			
			
}
