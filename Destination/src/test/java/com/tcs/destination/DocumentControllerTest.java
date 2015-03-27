package com.tcs.destination;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
	
	@Value("${fileBaseDir}")
	private String fileBasePath;
	
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
	
	@Test
	public void TestUpload() throws Exception {
		File resourcesDirectory = new File("src/test/java/com/tcs/destination/persistence.xml");
		String fullPath = resourcesDirectory.getAbsolutePath();
		
		Path path = Paths.get(resourcesDirectory.toURI());
		byte[] data = Files.readAllBytes(path);
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		 String fileUrl = "file://"+fullPath;
		String type = fileNameMap.getContentTypeFor(fileUrl);
		
		MockMultipartFile mockMultipartFile =
		       new MockMultipartFile("file", fullPath, type, data);
		mockMvc.perform(fileUpload("/document/upload?documentName=MyDoc&documentType=DOC&entityType=CUSTOMER&parentEntity=CUSTOMER&parentEntityId=CUS541&uploadedBy=541045&connectId=CNN1")
				.file(mockMultipartFile)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("Success"));
		
	}
	
	
	@Test
	public void TestDownload() throws Exception {
		mockMvc.perform(get("/document/download/DOC4")).andExpect(status().isOk());
		
	}
	
	@Test
	public void TestDelete() throws Exception {
		mockMvc.perform(put("/document/delete?docIds=DOC4,DOC5")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("Success"));
	}

}
