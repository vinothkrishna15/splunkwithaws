package com.tcs.destination.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.GeographyCountryMappingT;

import java.util.ArrayList;
import java.util.List;

import com.tcs.destination.data.repository.CountryRepository;

@Service
public class CountryService {
	
	private static final Logger logger = LoggerFactory.getLogger(CountryService.class);

	@Autowired
	CountryRepository countryRepository;

	public List<GeographyCountryMappingT> findAll() {
		logger.debug("Inside findAll Service");
		return (ArrayList<GeographyCountryMappingT>) countryRepository
				.findAll();
	}

}
