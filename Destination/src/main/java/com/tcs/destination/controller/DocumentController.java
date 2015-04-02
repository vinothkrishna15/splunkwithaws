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
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/document")
public class DocumentController {

	@Autowired
	DocumentService documentService;

	@Value("${fileBaseDir}")
	private String fileBasePath;
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
	
	// @RequestMapping(value = "/insert", method = RequestMethod.GET)
	// public @ResponseBody int ajaxConnectSearchById() throws IOException
	// {
	// FileInputStream fileInputStream=null;
	// File file = new
	// File("/Users/BNPP/Documents/my new pgm/Destination/src/test/java/com/tcs/textedit/MY_FUTURE_REF");
	// byte[] bFile = new byte[(int) file.length()];
	// fileInputStream = new FileInputStream(file);
	// fileInputStream.read(bFile);
	// fileInputStream.close();
	// // for (int i = 0; i < bFile.length; i++)
	// // {
	// // System.out.print((char)bFile[i]);
	// // }
	// DocumentRepositoryT docrep=new DocumentRepositoryT();
	// docrep.setDocumentType("retail");
	// docrep.setDocumentName("ABC RET");
	// docrep.setDocumentSearchKeywords("ABC");
	// docrep.setFileReference(bFile);
	// docrep.setEntityType("RETAIL DOC");
	// docrep.setParentEntity("AMERICA");
	// docrep.setParentEntityId("34343");
	// // docrep.setUploadedBy("886301");
	// docrep.setUploadedDatetime(new Timestamp(date.getTime()));
	// return documentService.save(docrep);
	// }

	@RequestMapping(value = "/download/{documentId}", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource>  download(
			@PathVariable("documentId") String documentId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		logger.info("Download Request received for id: " + documentId);
		DocumentRepositoryT document = documentService.findByDocumentId(documentId);
		if(document!=null){
			logger.info(documentId + " - Record Found");
		String fullPath = document.getFileReference();
		logger.info(documentId + " - File Found : " + fullPath);
		File file = new File(fullPath);
		String name = document.getDocumentName();
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentDispositionFormData("attachment", name);
	    logger.info(documentId + " - Download Header - Attachment : " + name);
	    FileNameMap fileNameMap = URLConnection.getFileNameMap();
	    String fileUrl = "file://"+fullPath;
	      String type = fileNameMap.getContentTypeFor(fileUrl);
	      if(type==null){
	    	  FileDataSource fds = new FileDataSource(file);
	    	  type = fds.getContentType();
	      }
	    respHeaders.setContentType(MediaType.valueOf(type));
	    
	    logger.info(documentId + " - Download Header - Mime Type : " + type);
	    InputStreamResource isr;
		try {
			isr = new InputStreamResource(new FileInputStream(file));
			logger.info("DOWNLOAD PROCESSED SUCCESSFULLY - " + documentId);
			return new ResponseEntity<InputStreamResource>(isr,respHeaders,HttpStatus.OK);
		} catch (FileNotFoundException e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,documentId + "Error processing the file");
		}
		}
		throw new DestinationException(HttpStatus.NOT_FOUND,documentId + "No Records Found");
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody String delete(@RequestParam(value = "docIds") String idsToDelete) throws Exception{
		logger.info("Deletion request Received for ids: " + idsToDelete);
		String[] docIds = idsToDelete.split(",");
		Status status = new Status();
			String deletedIds = documentService.deleteDocRecords(docIds);
			status.setStatus(Status.SUCCESS, "Files Deleted for " + deletedIds);
			logger.info("DELETE SUCCESS - Files Deleted for " + deletedIds);
			return Constants.filterJsonForFieldAndViews("all", "", status);
	}
		
		

	@RequestMapping(value = "/{documentId}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("documentId") String documentId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		DocumentRepositoryT docrep = documentService
				.findByDocumentId(documentId);
		return Constants.filterJsonForFieldAndViews(fields, view, docrep);
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
		logger.info("Upload request Received : docName - " + documentName + ", entityType" + entityType
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
           logger.info("UPLOAD SUCCESS - Record Created,  Id: " + docId);
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
//			status.setStatus(Status.FAILED, e.getMessage());
//			return Constants.filterJsonForFieldAndViews(fields, view, status);
		}

		return Constants.filterJsonForFieldAndViews(fields, view, status);
	}

}
