package com.tcs.destination.controller;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.DocumentsT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DocumentsService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This controller deals with the document related functionalities
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/doc")
public class DocumentsController {

	private static final Logger logger = LoggerFactory
			.getLogger(DocumentsController.class);
	
	@Autowired
	DocumentsService  documentsService ;
	/**
	 * This method is used to search a document based on documentId
	 * @param documentId
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/{documentId}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("documentId") String documentId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		try {
			logger.info("Inside document controller : Start of search");
			DocumentsT docrep = documentsService
					.findByDocumentId(documentId);
			logger.info("Inside document controller : End of search");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, docrep);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving document");
		}
	}
	
	/**
	 * This method is used to create a document
	 * 
	 * @param documentsT
	 * @param fields
	 * @param view
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> createDocument(
			@RequestBody DocumentsT documentsT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside DocumentsController: Start of create document");
		Status status = new Status();
		status.setStatus(Status.FAILED, "Save unsuccessful");
		try {
			documentsService.createDocument(documentsT,status);
           
			logger.info("Inside DocumentsController: End of create document");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in creating the document");
		}
	}
	

	/**
	 * To download the file
	 * @param documentsId
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadBFMFile(
			@RequestParam("documentsId") String documentsId)
			throws DestinationException {
		logger.info("Inside WorkflowController: Start of download doc");
		HttpHeaders respHeaders = null;
		InputStreamResource docStream = null;
		try {
			DocumentsT documentsT = documentsService.downloadFile(documentsId);
			docStream = new InputStreamResource(new ByteArrayInputStream(documentsT.getDocContent()));
			
			respHeaders = new HttpHeaders();
			String docName = documentsT.getDocName();
			respHeaders.add("documentName", docName);
			respHeaders.setContentDispositionFormData("attachment", docName);
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.info("Inside DocumentsController: Doc Downloaded Successfully ");
			return new ResponseEntity<InputStreamResource>(
					docStream, respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR : ", e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in downloading the document");
		}
	}
	
	/**
	 * This service is used to retrieve the details of document list
	 * @param page
	 * @param count
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/myDocumentList", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getMyWorklist(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside DocumentsController: Start of retrieving document list for a user");
		PageDTO<DocumentsT> documentList = new PageDTO<DocumentsT>();
		try {
			documentList = documentsService.getMyDocumentList(page,count);
			logger.info("Inside DocumentsController: End of retrieving document list for a user");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, documentList), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving document list for a user");
		}
	}


}
