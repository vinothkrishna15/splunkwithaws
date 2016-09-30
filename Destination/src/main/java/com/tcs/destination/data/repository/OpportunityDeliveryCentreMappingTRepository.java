package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityDeliveryCentreMappingT;

@Repository
public interface OpportunityDeliveryCentreMappingTRepository extends
		CrudRepository<OpportunityDeliveryCentreMappingT, Integer> {
	
	List<OpportunityDeliveryCentreMappingT> findByOpportunityId(String opportunityId);

	@Query(value = "select opportunity_delivery_centre_id from opportunity_delivery_centre_mapping_t  where opportunity_id = :opportunityId", nativeQuery=true)
	List<Integer> getIdByOpportunityId(@Param("opportunityId") String opportunityId);

}