package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
	
}
