package com.tcs.destination.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.data.repository.OfferingRepository;

@Service
public class OfferingService {
	
	private static final Logger logger = LoggerFactory.getLogger(OfferingService.class);

	@Autowired
	OfferingRepository offeringRepository;

	public ArrayList<OfferingMappingT> findAll() {
		logger.debug("Inside findAll service");
		return (ArrayList<OfferingMappingT>) offeringRepository.findAll();
	}

}
