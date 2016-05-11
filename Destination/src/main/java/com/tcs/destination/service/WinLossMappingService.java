package com.tcs.destination.service;

import java.util.List;

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
	 * This method is used to retrieve all Win Loss Mappings
	 */
	public List<WinLossFactorMappingT> findAllActive() {
		logger.debug("Inside findAll method of WinLossMappingService");
		return winLossMappingRepository.findByActiveTrue();
	}

}
