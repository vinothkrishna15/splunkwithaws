package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.GeographyCountryMappingT;

@Repository
public interface CountryRepository extends CrudRepository<GeographyCountryMappingT,String>{

	List<GeographyCountryMappingT> findByActiveTrue();
	
	GeographyCountryMappingT findByActiveTrueAndCountry(String country);
	
}
