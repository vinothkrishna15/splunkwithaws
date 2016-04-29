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

	@Query(value="select sales_support_owner from opportunity_sales_support_link_t where opportunity_sales_support_link_id = ?1",nativeQuery = true)
	String findSalesSupportOwner(String opportunitySalesSupportLinkId);
	
}