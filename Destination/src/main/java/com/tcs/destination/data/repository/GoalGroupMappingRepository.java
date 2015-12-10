package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.GoalGroupMappingT;
import com.tcs.destination.bean.GoalMappingT;
import com.tcs.destination.bean.UserGoalsT;

/**
 * 
 * Repository for working with {@link GoalMappingT} domain objects
 */
@Repository
public interface GoalGroupMappingRepository extends JpaRepository<GoalGroupMappingT, String> {
	
	@Query(value="select goal_id,user_group from goal_group_mapping_t;",nativeQuery=true)
	List<Object[]> findGoalGroup();
	
}
