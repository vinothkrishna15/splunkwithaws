package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.GoalMappingT;
import com.tcs.destination.bean.UserGoalsT;

/**
 * 
 * Repository for working with {@link GoalMappingT} domain objects
 */
@Repository
public interface GoalMappingRepository extends JpaRepository<GoalMappingT, String> {

	List<GoalMappingT> findByFinancialyearAndGoalId(String finyear,String goalId);
	
	@Query(value="select * from goal_mapping_t where goal_id in (select goal_id from goal_group_mapping_t where user_group=?1)",nativeQuery=true)
	List<Object[]> findGoalsByGroup(String userGroup);
	
	List<GoalMappingT> findByGoalName(String goalName);

	List<GoalMappingT> findByGoalNameAndFinancialyear(String cellValue,String finYear);
	
	@Query(value="select * from goal_mapping_t where financialyear=?1",nativeQuery=true)
	List<GoalMappingT> findByFinancialyear(String financialyear);
	
	@Query(value="select goal_id from goal_mapping_t where goal_name=?1",nativeQuery=true)
	String findGoalId(String goalName);
	
	@Query(value="select goal_id from goal_mapping_t where goal_name=?1 and financialyear=?2",nativeQuery=true)
	String findGoalIdByGoalNameAndFinancialYear(String cellValue,String finYear);
}
