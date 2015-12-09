package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;

@Repository
public interface OpportunityWinLossFactorsTRepository extends
		CrudRepository<OpportunityWinLossFactorsT, String> {

	List<OpportunityWinLossFactorsT> findByOpportunityId(String opportunityId);

}
