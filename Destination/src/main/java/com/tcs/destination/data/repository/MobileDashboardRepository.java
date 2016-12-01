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

}
