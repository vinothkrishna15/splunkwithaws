package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoSuchPartnerException;

@Component
public class PartnerService {

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;

	public PartnerMasterT findById(String partnerId) throws Exception {
		PartnerMasterT partner = partnerRepository.findOne(partnerId);
		if (partner == null)
			throw new DestinationException(HttpStatus.NOT_FOUND,"No such partner found.");
		return partner;
	}

	public List<PartnerMasterT> findByNameContaining(String chars) throws Exception {
		List<PartnerMasterT> partners=partnerRepository.findByPartnerNameIgnoreCaseLike("%" + chars
				+ "%");
		
		 if(partners.isEmpty())
			 throw new DestinationException(HttpStatus.NOT_FOUND,"No Partners found");
		 return partners;
	}

}