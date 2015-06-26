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
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside SearchKeywordsController /keywords/search?nameWith="+nameWith+" GET");
		List<String> keywords = keywordsService.findKeywordsWithNameContaining(nameWith);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, keywords), HttpStatus.OK);  
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
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside SearchKeywordsController /keywords/entity GET");
		List<SearchKeywordsT> keywords = keywordsService.findKeywordsByEntityTypeAndId(entityType, entityId);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, keywords), HttpStatus.OK);  
	}

}
