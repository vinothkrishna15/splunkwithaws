package com.tcs.destination.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.data.repository.CountryRepository;
import com.tcs.destination.data.repository.OfferingRepository;

@Component
public class OfferingService {

	@Autowired
	OfferingRepository offeringRepository;

	public ArrayList<OfferingMappingT> findAll() {
		return (ArrayList<OfferingMappingT>) offeringRepository.findAll();
	}

}
