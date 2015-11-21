package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.SalesStageMappingT;

@Repository
public interface SalesStageMappingRepository extends
		CrudRepository<SalesStageMappingT, Integer> {
	SalesStageMappingT findBySalesStageCode(Integer salesStageCode);
	
	@Query(value="select * from sales_stage_mapping_t",nativeQuery=true)
	List<SalesStageMappingT> getSalesStageCodes();
}
