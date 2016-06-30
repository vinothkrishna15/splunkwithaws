package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.IouBeaconMappingTRepository;

/*
 *This service retrieves all the data from iou_customer_mapping_t
 */
@Service
public class IOUService {
	
	private static final Logger logger = LoggerFactory.getLogger(IOUService.class);

	@Autowired
	CustomerIOUMappingRepository customerIOUMappingRepository;
	
	@Autowired
	IouBeaconMappingTRepository iouBeaconMappingTRepository;

	public List<IouCustomerMappingT> findAllActive() {
		logger.debug("Inside findAll() of IOUService");
		return customerIOUMappingRepository.findByActiveTrue();
	}
	
	/**
	 * Retrieves Iou Beacon Active IOUs 
	 * 
	 * @return
	 */
	public List<IouBeaconMappingT> findAllBeaconIouActive() {
		logger.debug("Inside findAllBeaconIouActive() of IOUService");
		return iouBeaconMappingTRepository.findByActiveTrue();
	}

}
