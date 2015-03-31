package com.tcs.destination;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
	
	MockMvc mockMvc=MockMvcBuilders.standaloneSetup(new DocumentController()).build();
	
@Before
	public void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(wvc).build();
	}

	@Test
	public void toTestGetDocumentUsingId() throws Exception {
		mockMvc.perform(get("/document/DOC8?fields=documentId,connectT,connectId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.documentId").value("DOC8"))
		.andExpect(jsonPath("$.connectT.connectId").value("CNN1"))
		.andDo(print())
		.andReturn();
	}
	

	@Test
	public void toTestNegativeInputs() throws Exception {
		mockMvc.perform(get("/document/DCO1?fields=documentIed,connecetT,conneectId").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
	
	@Test
	public void TestUpload() throws Exception {
		MockMultipartFile mockMultipartFile = getMultipartFile(TestConstants.testUploadFileLoc,"file");
		String fileExtension = getFileExtension(TestConstants.testUploadFileLoc);
		mockMvc.perform(fileUpload("/document?documentName=MyDoc"+fileExtension+"&documentType=DOC&entityType=CONNECT&parentEntity=CUSTOMER"
				+ "&parentEntityId=CUS541&uploadedBy=541045&connectId=CNN1")
				.file(mockMultipartFile)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("Success"));
		
	}

	private String getFileExtension(String testuploadfileloc) {
		File resourcesDirectory = new File(TestConstants.testUploadFileLoc);
		String fullPath = resourcesDirectory.getAbsolutePath();
		return fullPath.substring(fullPath.lastIndexOf("."), fullPath.length());
	}

	/*
	 * getMultipartFile - Returns the file in the format to upload. Accepts the file location, url param name as input.
	 * @return MockMultipartFile
	 * @param testuploadfileloc
	 * @param urlParamName
	 */
	private MockMultipartFile getMultipartFile(String testuploadfileloc, String urlParamName) throws Exception{
		File resourcesDirectory = new File(testuploadfileloc);
		
		String fullPath = resourcesDirectory.getAbsolutePath();
		
		String fileUrl = TestConstants.ftpPrefix+fullPath;
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String type = fileNameMap.getContentTypeFor(fileUrl);
		
		byte[] data = getBytes(resourcesDirectory);
		
		return new MockMultipartFile(urlParamName, fullPath, type, data);
	}

	private byte[] getBytes(File resourcesDirectory) throws Exception{
		Path path = Paths.get(resourcesDirectory.toURI());
		return Files.readAllBytes(path);
	}
	
	
	@Test
	public void TestDownload() throws Exception {
		mockMvc.perform(get("/document/download/DOC3")).andExpect(status().isOk());
	}
	
	@Test
	public void TestDelete() throws Exception {
		mockMvc.perform(delete("/document?docIds=DOC5")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("Success"));
	}

}
