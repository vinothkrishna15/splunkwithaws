package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.RevenueCustomerMappingT;

@Repository
public interface RevenueCustomerMappingTRepository extends CrudRepository<RevenueCustomerMappingT, String>{

	@Query(value="select * from revenue_customer_mapping_t where (finance_customer_name = ?1 and customer_geography = ?2 and finance_iou = ?3)", nativeQuery = true)
	List<RevenueCustomerMappingT> checkRevenueMappingPK(String financeCustomerName, String customerGeography, String financeIou);
}
