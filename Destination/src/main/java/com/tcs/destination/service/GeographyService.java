package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.data.repository.GeographyRepository;

/*
 *This service retrieves all the data from geography_mapping_t
 */
@Service
public class GeographyService {
	
	private static final Logger logger = LoggerFactory.getLogger(GeographyService.class);

	@Autowired
	GeographyRepository geographyRepository;

	public List<GeographyMappingT> findAll() {
		logger.info("Inside findAll() GeographyService");
		return (ArrayList<GeographyMappingT>) geographyRepository.findAll();
	}

}
