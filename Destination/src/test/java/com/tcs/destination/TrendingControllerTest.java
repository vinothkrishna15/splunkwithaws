package com.tcs.destination;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tcs.destination.controller.TrendingController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DestinationApplication.class)
@ContextConfiguration({"classpath:app-context.xml" })
@WebAppConfiguration
public class TrendingControllerTest {

	@Autowired 
	WebApplicationContext ctx;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	TrendingController trendingController;
	
	MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new TrendingController()).build(); 
	 
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
				.addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void TestTimeLine() throws Exception
	{
		mockMvc.perform(get("/trending").header("Authorization", "Basic YWFhOmJiYg==")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.bean[0].commentId").value("COC9"))
			.andExpect(jsonPath("$.bean[0].task.taskId").value("TAS9"))
			.andExpect(jsonPath("$.bean[1].commentId").value("COC139"))
			.andExpect(jsonPath("$.bean[2].commentId").value("COC122"))
			.andExpect(jsonPath("$.bean[3].commentId").value("COC121"))
			.andExpect(jsonPath("$.bean[4].commentId").value("COC100"))
			.andDo(print())
			.andReturn();
	}
	
	@Test
	public void TestTimeLineByCount() throws Exception
	{
		mockMvc.perform(get("/trending?count=4").header("Authorization", "Basic YWFhOmJiYg==")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.bean[0].commentId").value("COC9"))
			.andExpect(jsonPath("$.bean[0].task.taskId").value("TAS9"))
			.andExpect(jsonPath("$.bean[1].commentId").value("COC139"))
			.andExpect(jsonPath("$.bean[2].commentId").value("COC122"))
			.andExpect(jsonPath("$.bean[3].commentId").value("COC121"))
			.andDo(print())
			.andReturn();
	}
	
	
}
