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
	
	@Query(value = "select distinct display_sub_sp from sub_sp_mapping_t SUBSP "
			+ " join opportunity_sub_sp_link_t OPPSUBSP on SUBSP.sub_sp = OPPSUBSP.sub_sp where  subsp_primary=FALSE and OPPSUBSP.opportunity_id = ?1", nativeQuery = true)
	List<String> findSecondaryDisplaySubSpByOpportunityId(String opportunityId);

	@Query(value = "select distinct sub_sp from opportunity_sub_sp_link_t where subsp_primary=FALSE and opportunity_id = ?1", nativeQuery = true)
	List<String> findSecondarySubSpByOpportunityId(String opportunityId);
	
	@Query(value = "select distinct sub_sp from opportunity_sub_sp_link_t where subsp_primary=TRUE and opportunity_id = ?1", nativeQuery = true)
	String findPrimarySubSpByOpportunityId(String opportunityId);
	
	@Query(value = "select distinct display_sub_sp from sub_sp_mapping_t SUBSP "
			+ " join opportunity_sub_sp_link_t OPPSUBSP on SUBSP.sub_sp = OPPSUBSP.sub_sp where  subsp_primary=TRUE and OPPSUBSP.opportunity_id = ?1", nativeQuery = true)
	String findPrimaryDisplaySubSpByOpportunityId(String opportunityId);
	
}