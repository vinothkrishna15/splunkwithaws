package com.tcs.destination.data.repository;

import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_OUTCOME_DT_POST_QUERY;
import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_OUTCOME_DT_POST_SUPERVISOR;
import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_OUTCOME_DT_QUERY;
import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_TRGT_DT_POST_QUERY;
import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_TRGT_DT_POST_SUPERVISOR;
import static com.tcs.destination.utils.QueryConstants.BID_DETAILS_TRGT_DT_QUERY;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tcs.destination.bean.OpportunityT;

public interface BatchOpportunityRepository extends CrudRepository<OpportunityT, String> {

	@Query(value = BID_DETAILS_TRGT_DT_QUERY, nativeQuery = true)
	List<Object[]> getBidDetailsRemindersForTrgtDt();
	
	@Query(value = BID_DETAILS_OUTCOME_DT_QUERY, nativeQuery = true)
	List<Object[]> getBidDetailsRemindersForOutcomeDt();
	
	@Query(value = BID_DETAILS_TRGT_DT_POST_QUERY, nativeQuery = true)
	List<Object[]> getBidDetailsRemindersPostTrgtDt(@Param("remindForDays") Integer remindForDays);
	
	@Query(value = BID_DETAILS_OUTCOME_DT_POST_QUERY, nativeQuery = true)
	List<Object[]> getBidDetailsRemindersPostOutcomeDt(@Param("remindForDays") Integer remindForDays);
	
	@Query(value = BID_DETAILS_TRGT_DT_POST_SUPERVISOR, nativeQuery = true)
	List<Object[]> getBidDtRmdsPostTrgtDtSupervisor(@Param("remindForDays") Integer remindForDays);
	
	@Query(value = BID_DETAILS_OUTCOME_DT_POST_SUPERVISOR, nativeQuery = true)
	List<Object[]> getBidDtRmdsPostOutcomeDtSupervisor(@Param("remindForDays") Integer remindForDays);
	
	@Query(value = "select * from db_maintenance(:purgedays, :purgenotification, :purgecollaboration, :purgeyears)", nativeQuery = true)
    Integer maintainDBTables(@Param("purgedays") Integer btchPrugeDays, @Param("purgenotification") Integer btchPrugeNotification, @Param("purgecollaboration") Integer btchPrugeCollaboration, @Param("purgeyears") Integer btchPrugeYears);
    
	@Query(value = "select * from opportunity_shelving(:shelveDays, :shelveUpdateDays)", nativeQuery = true)
	Integer updateOpportunityToShelve(@Param("shelveDays") Integer shelveDays, @Param("shelveUpdateDays") Integer shelveUpdateDays);
	
}
