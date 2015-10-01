package com.tcs.destination.data.repository;

import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_TRGT_DT_QUERY;
import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_OUTCOME_DT_QUERY;
import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_TRGT_DT_POST_QUERY;
import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_OUTCOME_DT_POST_QUERY;
import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_TRGT_DT_POST_SUPERVISOR;
import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_OUTCOME_DT_POST_SUPERVISOR;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.OpportunityT;

public interface BatchOpportunityRepository extends CrudRepository<OpportunityT, String> {

	@Query(value = BID_DETAILS_TRGT_DT_QUERY, nativeQuery = true)
	List<Object[]> getBidDetailsRemindersForTrgtDt();
	
	@Query(value = BID_DETAILS_OUTCOME_DT_QUERY, nativeQuery = true)
	List<Object[]> getBidDetailsRemindersForOutcomeDt();
	
	@Query(value = BID_DETAILS_TRGT_DT_POST_QUERY, nativeQuery = true)
	List<Object[]> getBidDetailsRemindersPostTrgtDt();
	
	@Query(value = BID_DETAILS_OUTCOME_DT_POST_QUERY, nativeQuery = true)
	List<Object[]> getBidDetailsRemindersPostOutcomeDt();
	
	@Query(value = BID_DETAILS_TRGT_DT_POST_SUPERVISOR, nativeQuery = true)
	List<Object[]> getBidDtRmdsPostTrgtDtSupervisor();
	
	@Query(value = BID_DETAILS_OUTCOME_DT_POST_SUPERVISOR, nativeQuery = true)
	List<Object[]> getBidDtRmdsPostOutcomeDtSupervisor();

}
