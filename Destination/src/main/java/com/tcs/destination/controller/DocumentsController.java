package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.bean.DocumentsT;
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

}
