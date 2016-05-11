package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.OfferingMappingT;

public interface OfferingRepository extends
		CrudRepository<OfferingMappingT, String> {

    @Query(value="select offering from offering_mapping_t", nativeQuery=true)
    List<String> getOffering();
    
    @Query(value="select subSp, offering from OfferingMappingT")
    List<Object[]> getSubSpOffering();
    
    OfferingMappingT findByOffering(String offering);
    
    List<OfferingMappingT> findByActive(String active);

	OfferingMappingT findByActiveTrueAndOffering(String offering);

}
