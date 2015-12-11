package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunitySubSpLinkT;

@Repository
public interface OpportunitySubSpLinkTRepository extends
		CrudRepository<OpportunitySubSpLinkT, String> {

	List<OpportunitySubSpLinkT> findByOpportunityId(String opportunityId);
	
	@Query(value = "select sub_sp from opportunity_sub_sp_link_t  where opportunity_id = ?1", nativeQuery = true)
	List<String> findSubSpByOpportunityId(String opportunityId);

}