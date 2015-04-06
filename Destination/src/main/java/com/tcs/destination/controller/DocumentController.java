package com.tcs.destination.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.FileNameMap;
import java.net.URLConnection;

import javax.activation.FileDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DocumentService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/document")
public class DocumentController {

	@Autowired
	DocumentService documentService;

	@Value("${fileBaseDir}")
	private String fileBasePath;
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
	

	@RequestMapping(value = "/download/{documentId}", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource>  download(
			@PathVariable("documentId") String documentId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		logger.debug("Download Request received for id: " + documentId);
		DocumentRepositoryT document = documentService.findByDocumentId(documentId);
		if(document!=null){
			logger.debug(documentId + " - Record Found");
		String fullPath = document.getFileReference();
		logger.debug(documentId + " - File Found : " + fullPath);
		File file = new File(fullPath);
		String name = document.getDocumentName();
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentDispositionFormData("attachment", name);
	    logger.debug(documentId + " - Download Header - Attachment : " + name);
	    FileNameMap fileNameMap = URLConnection.getFileNameMap();
	    String fileUrl = "file://"+fullPath;
	      String type = fileNameMap.getContentTypeFor(fileUrl);
	      if(type==null){
	    	  FileDataSource fds = new FileDataSource(file);
	    	  type = fds.getContentType();
	      }
	    respHeaders.setContentType(MediaType.valueOf(type));
	    
	    logger.debug(documentId + " - Download Header - Mime Type : " + type);
	    InputStreamResource isr;
		try {
			isr = new InputStreamResource(new FileInputStream(file));
			logger.debug("DOWNLOAD PROCESSED SUCCESSFULLY - " + documentId);
			return new ResponseEntity<InputStreamResource>(isr,respHeaders,HttpStatus.OK);
		} catch (FileNotFoundException e) {
			logger.error("INTERNAL_SERVER_ERROR: Error processing the file");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,documentId + "Error processing the file");
		}
		}
		logger.error("NOT_FOUND: No Records Found");
		throw new DestinationException(HttpStatus.NOT_FOUND,documentId + "No Records Found");
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody String delete(@RequestParam(value = "docIds") String idsToDelete) throws Exception{
		logger.debug("Deletion request Received for ids: " + idsToDelete);
		String[] docIds = idsToDelete.split(",");
		Status status = new Status();
			String deletedIds = documentService.deleteDocRecords(docIds);
			status.setStatus(Status.SUCCESS, "Files Deleted for " + deletedIds);
			logger.debug("DELETE SUCCESS - Files Deleted for " + deletedIds);
			return ResponseConstructors.filterJsonForFieldAndViews("all", "", status);
	}
		
		

	@RequestMapping(value = "/{documentId}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("documentId") String documentId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		logger.debug("Inside DocumentController /document/documentId="+documentId+" GET");
		DocumentRepositoryT docrep = documentService
				.findByDocumentId(documentId);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, docrep);
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody String upload(
			@RequestParam("documentName") String documentName,
			@RequestParam("documentType") String documentType,
			@RequestParam("entityType") String entityType,
			@RequestParam("parentEntity") String parentEntity,
			@RequestParam("parentEntityId") String parentEntityId,
			@RequestParam(value = "commentId", defaultValue = "") String commentId,
			@RequestParam(value = "connectId", defaultValue = "") String connectId,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "opportunityId", defaultValue = "") String opportunityId,
			@RequestParam(value = "partnerId", defaultValue = "") String partnerId,
			@RequestParam(value = "taskId", defaultValue = "") String taskId,
			@RequestParam("uploadedBy") String uploadedBy,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		logger.debug("Upload request Received : docName - " + documentName + ", entityType" + entityType
				+ ", uploadedBy" + uploadedBy);
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			String docId = documentService.saveDocument(documentName,
				 documentType, entityType,
					parentEntity, parentEntityId, commentId, connectId,
					customerId, opportunityId, partnerId, taskId, uploadedBy,
					file);
			status.setStatus(Status.SUCCESS, "Id : " + docId);
           logger.debug("UPLOAD SUCCESS - Record Created,  Id: " + docId);
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" +e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
//			status.setStatus(Status.FAILED, e.getMessage());
//			return Constants.filterJsonForFieldAndViews(fields, view, status);
		}

		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, status);
	}

}
