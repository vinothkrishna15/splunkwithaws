package com.tcs.destination;

import java.lang.reflect.Member;
import java.nio.charset.Charset;
import java.util.*;

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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
import com.tcs.destination.bean.MyFavorites;
import com.tcs.destination.controller.FavoritesController;
import com.tcs.destination.controller.UserDetailsController;
import com.tcs.destination.service.FavoritesService;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:app-context.xml")
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@WebAppConfiguration
public class MyFavoritesControllerTest {

	@Autowired
	FavoritesService myFavService;

	@Autowired
	WebApplicationContext wvc5;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;

	MockMvc mockMvcuser = MockMvcBuilders.standaloneSetup(
			new FavoritesController()).build();

	@Before
	public void setUp() throws Exception {
		mockMvcuser = MockMvcBuilders.webAppContextSetup(wvc5)
				.addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void test() throws Exception {
		mockMvcuser
				.perform(
						get("/favorites?entityType=CUSTOMER&fields=userFavoritesId,createdDatetime,entityType,customerMasterT,customerId,createdModifiedBy,customerName")
								.header("Authorization", "Basic YWFhOmJiYg==")
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].userFavoritesId").value("USF1"))
				// .andExpect(jsonPath("$.createdDatetime").value("2015-02-03 02:41:06.0"))
				.andExpect(jsonPath("$[0].entityType").value("CUSTOMER"))
				// .andExpect(jsonPath("$.customerMasterT.customerId").value("287693"))
				// .andExpect(jsonPath("$.customerMasterT.customerName").value("Apoteket AB"))
				.andDo(print()).andReturn();

		// List<MyFavorites> myfav=myFavService.findFavoritesFor(null,
		// "541045");
		// assertEquals(myfav.get(0).getId(),"CUS6");
		// assertNull(myfav.get(0).getLogo());

		// for negative inputs
		// List<MyFavorites> myfavneg=myFavService.findFavoritesFor("534354");
		// assertNull(myfavneg);

	}

}
