package com.tcs.destination.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.WinLossFactorMappingT;
import com.tcs.destination.data.repository.WinLossMappingRepository;

/**
 * 
 * This service is used to handle Win Loss Mapping requests
 *
 */
@Service
public class WinLossMappingService {

	private static final Logger logger = LoggerFactory
			.getLogger(WinLossMappingService.class);

	@Autowired
	WinLossMappingRepository winLossMappingRepository;

	/**
	 * 
	 * This method is used to retrieve Win Loss Mapping
	 */
	public ArrayList<WinLossFactorMappingT> findAll() {
		logger.debug("Inside findAll Service");
		return (ArrayList<WinLossFactorMappingT>) winLossMappingRepository
				.findAll();
	}

}
