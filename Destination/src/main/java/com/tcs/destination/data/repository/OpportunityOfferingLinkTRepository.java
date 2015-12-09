package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.OpportunityOfferingLinkT;

/**
 * 
 * Repository for working with {@link ConnectT} domain objects
 */
@Repository
public interface OpportunityOfferingLinkTRepository extends CrudRepository<OpportunityOfferingLinkT, String> {

	List<com.tcs.destination.bean.OpportunityOfferingLinkT> findByOpportunityId(
			String opportunityId);

}
