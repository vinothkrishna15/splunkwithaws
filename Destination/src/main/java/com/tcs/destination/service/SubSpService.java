package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.PartnerSubSpMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.data.repository.PartnerSubSpMappingTRepository;
import com.tcs.destination.data.repository.SubSpRepository;

/**
 *This class retrieves all the data from subsp_mapping_t 
 *
 */
@Service
public class SubSpService {
	
	private static final Logger logger = LoggerFactory.getLogger(SubSpService.class);
	
	@Autowired
	SubSpRepository subSpRepository;
	
	@Autowired
	PartnerSubSpMappingTRepository partnerSubSpMappingTRepository;

	public List<SubSpMappingT> findAllActive() {
		logger.debug("Inside findAllActive() SubSpService");
		return subSpRepository.findByActiveTrue();
	}

	public List<PartnerSubSpMappingT> findByPartner(String partnerId) {
		logger.debug("Inside findByPartner() SubSpService");
		return partnerSubSpMappingTRepository.findByPartnerId(partnerId);
	}

	public SubSpMappingT findBySubspAndActive(Integer subSpId) {
		logger.debug("Inside findBySubspAndActive() SubSpService");
		return subSpRepository.findBySubSpIdAndActiveTrue(subSpId);
	}
	
	
}
