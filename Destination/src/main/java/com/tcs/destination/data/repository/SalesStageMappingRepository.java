package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.SalesStageMappingT;

@Repository
public interface SalesStageMappingRepository extends
		CrudRepository<SalesStageMappingT, Integer> {
	SalesStageMappingT findBySalesStageCode(Integer salesStageCode);
}
