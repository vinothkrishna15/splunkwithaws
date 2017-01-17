package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.MobileDashboardComponentT;

@Repository
public interface MobileDashboardComponentRepository extends
		CrudRepository<MobileDashboardComponentT, Integer> {

	List<MobileDashboardComponentT> findByCategoryId(int dashboardCategory);

	

}
