package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunityT;

@Repository
public interface OpportunitySalesSupportLinkTRepository extends
		CrudRepository<OpportunitySalesSupportLinkT, String> {

	@Query("select ossl.opportunityT from OpportunitySalesSupportLinkT ossl where ossl.salesSupportOwner=(:userId)")
	List<OpportunityT> findOpportunityTByUserId(@Param("userId") String userId);

	List<OpportunitySalesSupportLinkT> findByOpportunityId(String opportunityId);
	
}