package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;

/*
 *This service retrieves all the data from iou_customer_mapping_t
 */
@Service
public class IOUService {
	
	private static final Logger logger = LoggerFactory.getLogger(IOUService.class);

	@Autowired
	CustomerIOUMappingRepository customerIOUMappingRepository;

	public List<IouCustomerMappingT> findAll() {
		logger.debug("Inside findAll() of IOUService");
		return (ArrayList<IouCustomerMappingT>) customerIOUMappingRepository
				.findAll();
	}

}
