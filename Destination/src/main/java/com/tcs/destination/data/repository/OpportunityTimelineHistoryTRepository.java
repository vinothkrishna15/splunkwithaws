package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityTimelineHistoryT;

@Repository
public interface OpportunityTimelineHistoryTRepository extends
		CrudRepository<OpportunityTimelineHistoryT, String> {

	List<OpportunityTimelineHistoryT> findByOpportunityId(String opportunityId);
}
