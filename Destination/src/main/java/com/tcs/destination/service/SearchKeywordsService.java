package com.tcs.destination.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.SearchKeywordsT;
import com.tcs.destination.data.repository.SearchKeywordsRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;

/**
 * Service class to handle Keyword Search related requests.
 * 
 */
@Service
public class SearchKeywordsService {

	private static final Logger logger = LoggerFactory.getLogger(SearchKeywordsService.class);
	
	@Autowired
	SearchKeywordsRepository searchKeywordsRepository;

	/**
	 * This method is used to find all search keywords matching the given keyword
	 * @param keyword
	 * @return keywords
	 */
	public List<String> findKeywordsWithNameContaining(String keyword) throws Exception {
		logger.info("Begin: Inside findKeywordsWithNameContaining SearchKeywordsService");
		List<String> keywords = searchKeywordsRepository.findKeywordsWithNameContaining("%" + keyword + "%");
		if ((keywords == null) || keywords.isEmpty()) {
			logger.error("NOT_FOUND: No keywords found with the given keyword");
			throw new DestinationException(
					HttpStatus.NOT_FOUND, "No keywords found with the given keyword");
		}
		logger.info("End: Inside findKeywordsWithNameContaining SearchKeywordsService");
		return keywords;
	}

	/**
	 * This method is used to find all search keywords for a given Entity Type & Id
	 *
	 * @param entityType, entityId
	 * @return keywords
	 */
	public List<SearchKeywordsT> findKeywordsByEntityTypeAndId(String entityType, String entityId) throws Exception {
		logger.info("Begin: Inside findKeywordsByEntityTypeAndId SearchKeywordsService");

		if (!EntityType.contains(entityType)) {
			logger.error("Invalid Entity Type");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Entity Type");
		}
		
		List<SearchKeywordsT> keywords = searchKeywordsRepository.findByEntityTypeAndEntityId(entityType, entityId);
		if ((keywords == null) || keywords.isEmpty()) {
			logger.error("NOT_FOUND: No keywords found with the given Entity Type & Id");
			throw new DestinationException(
					HttpStatus.NOT_FOUND, "No keywords found with the given Entity Type & Id");
		}
		logger.info("End: Inside findKeywordsByEntityTypeAndId SearchKeywordsService");
		return keywords;
	}
}