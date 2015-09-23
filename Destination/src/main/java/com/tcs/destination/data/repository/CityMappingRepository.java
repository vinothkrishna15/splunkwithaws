package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.CityMapping;

@Repository
public interface CityMappingRepository extends
		CrudRepository<CityMapping, String> {
	
	
	@Query(value ="select * from city_mapping where upper(city)= ?1",
			 nativeQuery = true)
	CityMapping getCityByCityName(String city);

}
