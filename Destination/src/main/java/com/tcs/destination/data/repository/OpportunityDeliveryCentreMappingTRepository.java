package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityDeliveryCentreMappingT;

@Repository
public interface OpportunityDeliveryCentreMappingTRepository extends
		CrudRepository<OpportunityDeliveryCentreMappingT, Integer> {
	
	List<OpportunityDeliveryCentreMappingT> findByOpportunityId(String opportunityId);

}