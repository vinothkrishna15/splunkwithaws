package com.tcs.destination.data.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserGoalsT;

/**
 * 
 * Repository for working with {@link UserGoalsT} domain objects
 */
@Repository
public interface UserGoalsRepository extends JpaRepository<UserGoalsT, String> {
	
	@Query(value = "select u.user_id,u.user_name,u.user_group,g.goal_name,ugl.financial_year,ugl.target_value from user_t u"
			+" join user_goals_t ugl on ugl.user_id=u.user_id "
			+"join goal_mapping_t g on ugl.goal_id=g.goal_id", nativeQuery = true)
	List<Object[]> getGoalsWithUserName();
	
    @Query(value = "select * from user_goals_t where user_id=?1 and goal_id=?2 and financial_year=?3", nativeQuery = true)
	List<UserGoalsT> getUserGoals(String userIdGoalSheet,String goalId,String financialYear);
   
    UserGoalsT  findByGoalId(String goalId);
    
   List<UserGoalsT> findByUserId(String userId);
}
