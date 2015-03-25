package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.GeographyCountryMappingT;

public interface CountryRepository extends CrudRepository<GeographyCountryMappingT,String>{
}
