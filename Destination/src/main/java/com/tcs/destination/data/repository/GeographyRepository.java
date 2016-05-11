package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.GeographyMappingT;

@Repository
public interface GeographyRepository extends
		CrudRepository<GeographyMappingT, String> {

	@Query(value = "select distinct(GMT.geography) from  geography_mapping_t GMT where display_geography in (:geoList)" ,nativeQuery =  true)
	List<String> findByDisplayGeography(@Param("geoList") List<String> geoList);
	
	GeographyMappingT findByGeography(String geography);

	@Query(value = "select country from geography_country_mapping_t" ,nativeQuery =  true)
	List<String> getCountry();

	List<GeographyMappingT> findByActiveTrue();

}
