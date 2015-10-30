package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityPartnerLinkT;

/**
 * @author bnpp
 *
 */
@Repository
public interface OpportunityPartnerLinkTRepository extends
		CrudRepository<OpportunityPartnerLinkT, String> {


	@Query(value ="select * from opportunity_partner_link_t where partner_id=?1",nativeQuery = true)
	List<OpportunityPartnerLinkT> findByPartnerId(String partnerId);
	

	
	@Query(value ="select o.opportunityPartnerLinkId from OpportunityPartnerLinkT o")
	List<String> findOpportunityPartnerLinkIdFromOpportunityPartnerLinkT();
}
