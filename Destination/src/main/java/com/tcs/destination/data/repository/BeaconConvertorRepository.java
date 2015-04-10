package com.tcs.destination.data.repository;

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

}
