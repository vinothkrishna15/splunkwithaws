package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.data.repository.GeographyRepository;

@Service
public class GeographyService {
	
	private static final Logger logger = LoggerFactory.getLogger(GeographyService.class);

	@Autowired
	GeographyRepository geographyRepository;

	public List<GeographyMappingT> findAll() {
		logger.debug("Inside findAll Service");
		return (ArrayList<GeographyMappingT>) geographyRepository.findAll();
	}

}
