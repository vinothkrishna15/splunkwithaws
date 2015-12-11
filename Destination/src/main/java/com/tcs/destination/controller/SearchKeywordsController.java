package com.tcs.destination.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.SearchKeywordsT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.SearchKeywordsService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle Task module related requests.
 * 
 */
@RestController
@RequestMapping("/keywords")
public class SearchKeywordsController {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchKeywordsController.class);

	@Autowired
	SearchKeywordsService keywordsService;

	/**
	 * This method is used to find all search keywords matching the given keyword
	 *
	 * @param keyword
	 * @return keywords
	 */
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findKeywordsWithName(
			@RequestParam(value="nameWith") String nameWith,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Inside SearchKeywordsController / Start of retrieving search keywords matching the given keyword");
		try {
		List<String> keywords = keywordsService.findKeywordsWithNameContaining(nameWith);
		logger.info("Inside SearchKeywordsController / End of retrieving search keywords matching the given keyword");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, keywords), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the keywords matching the given keyword :" + nameWith);
	   }
	}
	
	/**
	 * This method is used to find all search keywords for a given Entity Type & Id
	 *
	 * @param entityType, entityId
	 * @return keywords
	 */
	@RequestMapping(value="/entity", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findKeywordsByEntityTypeAndId(
			@RequestParam(value="entityType") String entityType,
			@RequestParam(value="entityId") String entityId,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Inside SearchKeywordsController / Start of retrieving search keywords for a given Entity Type & Id");
		try {
		List<SearchKeywordsT> keywords = keywordsService.findKeywordsByEntityTypeAndId(entityType, entityId);
		logger.info("Inside SearchKeywordsController / End of retrieving search keywords for a given Entity Type & Id");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, keywords), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving search keywords for a given Entity Type :" + entityType + "Entity Id" + entityId);
	   }
	}

}
