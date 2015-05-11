package com.tcs.destination.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.WinLossFactorMappingT;
import com.tcs.destination.data.repository.WinLossMappingRepository;

@Component
public class WinLossMappingService {

	private static final Logger logger = LoggerFactory
			.getLogger(WinLossMappingService.class);

	@Autowired
	WinLossMappingRepository winLossMappingRepository;

	public ArrayList<WinLossFactorMappingT> findAll() {
		logger.debug("Inside findAll Service");
		return (ArrayList<WinLossFactorMappingT>) winLossMappingRepository
				.findAll();
	}

}
