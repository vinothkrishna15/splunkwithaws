package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.tcs.destination.bean.MobileDashboardT;

@Repository
public interface MobileDashboardRepository extends CrudRepository<MobileDashboardT, Integer> {
	
	

	List<MobileDashboardT> findByUserIdAndDashboardCategoryOrderByOrderNumberAsc(
			String userId, int dashboardCategory);

}
