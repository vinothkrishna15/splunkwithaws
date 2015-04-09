package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CompetitorMappingT;
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.exception.DestinationException;

@Component
public class CompetitorService {
	
	private static final Logger logger = LoggerFactory.getLogger(CompetitorService.class);

	@Autowired
	CompetitorRepository compRepository;
	
	public List<CompetitorMappingT> findByNameContaining(String chars) throws Exception {
		List<CompetitorMappingT> compList = compRepository
				.findByCompetitorNameIgnoreCaseLike("%" + chars + "%");
		if (compList.isEmpty())
		{
			logger.error("NOT_FOUND: No such competitor");
			throw new DestinationException(HttpStatus.NOT_FOUND,"No such competitor");
		}
		return compList;

	}

}