package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryMasterManagerLinkT;

@Repository
public interface DeliveryMasterManagerLinkRepository extends PagingAndSortingRepository<DeliveryMasterManagerLinkT, Integer> {
	
	List<DeliveryMasterManagerLinkT> findByDeliveryManagerId(String managerId);
	
	List<DeliveryMasterManagerLinkT> findByDeliveryMasterId(String engagementId);
	
}
