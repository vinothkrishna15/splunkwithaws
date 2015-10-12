package com.tcs.destination.data.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BdmTargetT;

/**
 * @author bnpp
 *
 */
@Repository
public interface BdmTargetTRepository extends
		CrudRepository<BdmTargetT, String> {

	@Query(value = "select sum(target) from bdm_target_t where bdm_user_id=?1 and year = ?2", nativeQuery = true)
	List<BigDecimal> findSumOfTargetByBdmTargetIdAndYear(String bdmTargetId,
			String financialYear);
	
	/**
	 * This query gets the sum of all targets in bdm_target_t table for users under a supervisor 
	 * 
	 * @param users
	 * @param financialYear
	 * @return
	 */
	@Query(value = "select sum(target) from bdm_target_t where bdm_user_id in (:users) and year = (:year)", nativeQuery = true)
	List<BigDecimal> findSumOfTargetBySubordinatesPerSupervisorAndYear(@Param("users") List<String> users, @Param("year") String financialYear);
	
	
	
	@Query(value = " select distinct ugt.goal_id,user_id,target_value from user_goals_t  ugt "
			+ " join goal_group_mapping_t ggmt on ugt.goal_id=ggmt.goal_id and ggmt.is_active='Y' "
			+ " where ugt.goal_id in ('G1','G2','G3') and user_id=(:userId) and financial_year=(:financialYear) "
			+ " order by goal_id ", nativeQuery = true)
	List<Object[]> findBDMOrSupervisorTargetGoalsByUserIdAndYear(
			@Param("userId") String userId,
			@Param("financialYear") String financialYear);

	@Query(value = " select distinct goal_id,goal_name,default_target from goal_mapping_t where goal_id in ('G1','G2','G3') "
			+ " and financialyear=(:financialYear) order by goal_id ", nativeQuery = true)
	List<Object[]> findBDMOrSupervisorRefTargetGoalsByYear(
			@Param("financialYear") String financialYear);
	
	@Query(value = " select distinct target_value from user_goals_t  ugt "
			+ " join goal_group_mapping_t ggmt on ugt.goal_id=ggmt.goal_id and ggmt.is_active='Y' "
			+ " where ugt.goal_id = (:goalId) and user_id=(:userId) and financial_year=(:financialYear) ", nativeQuery = true)
	BigDecimal findBDMOrSupervisorTargetGoalsByUserIdAndYear(
			@Param("userId") String userId,
			@Param("goalId") String goalId,
			@Param("financialYear") String financialYear);
	
	@Query(value = " select default_target from goal_mapping_t where goal_id = (:goalId) "
			+ " and financialyear=(:financialYear) ", nativeQuery = true)
	BigDecimal findBDMOrSupervisorRefTargetGoalsByYear(
			@Param("goalId") String goalId,
			@Param("financialYear") String financialYear);
	
}
