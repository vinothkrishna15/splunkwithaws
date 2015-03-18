package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.exception.NoSuchPartnerException;

@Component
public class PartnerService {

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;

	public PartnerMasterT findById(String partnerId) {
		PartnerMasterT partner = partnerRepository.findOne(partnerId);
		if (partner == null)
			throw new NoSuchPartnerException();
		return partner;
	}

	public List<PartnerMasterT> findByNameContaining(String chars) {
		return partnerRepository.findByPartnerNameIgnoreCaseLike("%" + chars
				+ "%");
	}

}