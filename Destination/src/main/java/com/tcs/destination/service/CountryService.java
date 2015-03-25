package com.tcs.destination.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.GeographyCountryMappingT;

import java.util.ArrayList;
import java.util.List;

import com.tcs.destination.data.repository.CountryRepository;

@Component
public class CountryService {

	@Autowired
	CountryRepository countryRepository;

	public List<GeographyCountryMappingT> findAll() {
		return (ArrayList<GeographyCountryMappingT>) countryRepository
				.findAll();
	}

}
