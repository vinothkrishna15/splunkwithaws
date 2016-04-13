package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.RevenueCustomerMappingT;

@Repository
public interface RevenueCustomerMappingTRepository extends CrudRepository<RevenueCustomerMappingT, Long>{

	@Query(value="select * from revenue_customer_mapping_t where (finance_customer_name = ?1 and customer_geography = ?2 and finance_iou = ?3)", nativeQuery = true)
	List<RevenueCustomerMappingT> checkRevenueMappingPK(String financeCustomerName, String customerGeography, String financeIou);
	
	@Query(value="select * from revenue_customer_mapping_t where customer_id in(select customer_id from customer_master_t where customer_name in(select customer_name from workflow_customer_t where workflow_customer_id in (select entity_id from workflow_request_t where request_id = ?1 and status ='APPROVED' and entity_type_id =0 )))", nativeQuery = true)
	List<RevenueCustomerMappingT> getRevenueCustomerMappingForWorkflowCustomer(Integer requestId);
	
	@Query(value="select revenue_customer_map_id from revenue_customer_mapping_t where (finance_customer_name = ?1 and customer_geography = ?2 and finance_iou = ?3)", nativeQuery = true)
	Long findrevenueCustomerMapId(String financeCustomerName, String customerGeography, String financeIou);
	
	@Query(value="select * from revenue_customer_mapping_t where revenue_customer_map_id = ?1", nativeQuery = true)
	RevenueCustomerMappingT findByRevenueCustomerMapId(Long revenueCustomerMapId);
	
	@Query(value="select * from revenue_customer_mapping_t where customer_id = ?1", nativeQuery = true)
	List<RevenueCustomerMappingT> findByCustomerId(String customerId);
}
