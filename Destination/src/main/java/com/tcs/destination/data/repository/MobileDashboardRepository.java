package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.tcs.destination.bean.MobileDashboardT;

@Repository
public interface MobileDashboardRepository extends
		CrudRepository<MobileDashboardT, Integer> {

	List<MobileDashboardT> findByUserIdAndDashboardCategoryOrderByOrderNumberAsc(
			String userId, int dashboardCategory);

	@Modifying
	@Query(value = "delete from MobileDashboardT where "
			+ "dashboardCategory = (:category) and userId = (:userId)")
	void deleteByDashboardCategoryAndUserId(@Param("category") Integer category,@Param("userId") String userId);

	@Query(value = "select * from mobile_dashboard_t "
			+ "where user_id = (:userId) and dashboard_category = 1 "
			+ "order by order_number ASC limit 3", nativeQuery = true)
	List<MobileDashboardT> getFirstThreeComponentsInHealthCard(@Param("userId") String userId);

	long countByUserId(String userId);

	@Query(value = "select component_id from mobile_dashboard_t where user_id = (:userId) ", nativeQuery = true)
	List<Integer> getComponentsByUserId(@Param("userId") String userId);
}

