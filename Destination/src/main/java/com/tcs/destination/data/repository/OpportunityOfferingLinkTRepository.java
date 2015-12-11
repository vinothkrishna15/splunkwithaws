package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
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

	@Query(value = "select offering from opportunity_offering_link_t  where opportunity_id = ?1", nativeQuery = true)
	List<String> findOfferingByOpportunityId(String opportunityId);
}
