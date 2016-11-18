package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryMasterManagerLinkT;

@Repository
public interface DeliveryMasterManagerLinkRepository extends PagingAndSortingRepository<DeliveryMasterManagerLinkT, Integer> {
	
	List<DeliveryMasterManagerLinkT> findByDeliveryManagerId(String managerId);
	
	List<DeliveryMasterManagerLinkT> findByDeliveryMasterId(String engagementId);
	
	@Query(value="select user_name from user_t where user_id in (select distinct(delivery_manager_id) from  delivery_master_manager_link_t  "
			+ " where delivery_master_id =?1)",nativeQuery=true)
	List<String> getDeliveryManagersByEngagementId(String deliveryMasterId);
	
	@Query(value = "SELECT deliveryMasterId from DeliveryMasterManagerLinkT where deliveryManagerId = :managerId")
	List<String> findDeliveryIdsByManagerId(@Param("managerId")  String managerId);
	
}
