package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BeaconConvertorMappingT;

/**
 * @author bnpp
 *
 */
@Repository
public interface BeaconConvertorRepository extends CrudRepository<BeaconConvertorMappingT, String> {
	
	BeaconConvertorMappingT findByCurrencyName(String currencyname);

	@Query(value="select currencyName, conversionRate from BeaconConvertorMappingT")
	List<Object[]> getCurrencyNameAndRate();
}
