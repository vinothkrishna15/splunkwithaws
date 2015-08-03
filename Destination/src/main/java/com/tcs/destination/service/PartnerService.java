package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.exception.DestinationException;

@Service
public class PartnerService {
	
	private static final Logger logger = LoggerFactory.getLogger(PartnerService.class);

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;

	public PartnerMasterT findById(String partnerId) throws Exception {
		logger.debug("Inside findById Service");
		PartnerMasterT partner = partnerRepository.findOne(partnerId);
		if (partner == null)
		{
			logger.error("NOT_FOUND: No such partner found.");
			throw new DestinationException(HttpStatus.NOT_FOUND,"No such partner found.");
		}
		return partner;
	}

	public List<PartnerMasterT> findByNameContaining(String nameWith) throws Exception {
		logger.debug("Inside findByNameContaining Service");
		List<PartnerMasterT> partners = partnerRepository.findByPartnerNameIgnoreCaseContainingOrderByPartnerNameAsc(nameWith);
		
		if (partners.isEmpty()) {
			 logger.error("NOT_FOUND: No Partners found");
			 throw new DestinationException(HttpStatus.NOT_FOUND,"No Partners found");
		 }
		 return partners;
	}

	public List<PartnerMasterT> findByNameStarting(String startsWith) throws Exception {
		logger.debug("Inside findByNameContaining Service");
		List<PartnerMasterT> partners = partnerRepository.
				findByPartnerNameIgnoreCaseStartingWithOrderByPartnerNameAsc(startsWith);
		
		if (partners.isEmpty()) {
			 logger.error("NOT_FOUND: No Partners found");
			 throw new DestinationException(HttpStatus.NOT_FOUND,"No Partners found");
		 }
		 return partners;
	}

}