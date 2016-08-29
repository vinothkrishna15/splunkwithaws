package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.GeographyCountryMappingT;

@Repository
public interface GeographyCountryRepository extends
CrudRepository<GeographyCountryMappingT, String> {

    @Query(value="select geography, country from GeographyCountryMappingT")
    List<Object[]> getGeographyCountry();
    
    GeographyCountryMappingT findByCountry(String country);

}
