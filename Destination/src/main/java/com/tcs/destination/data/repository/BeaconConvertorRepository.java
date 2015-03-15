package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.ContactT;
import java.lang.String;
import java.util.List;

/**
 * @author bnpp
 *
 */
public interface BeaconConvertorRepository extends CrudRepository<BeaconConvertorMappingT, String> {
	
	BeaconConvertorMappingT findByCurrencyName(String currencyname);

}
