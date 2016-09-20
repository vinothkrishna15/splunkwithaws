package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryMasterManagerLinkT;
import com.tcs.destination.bean.DeliveryMasterT;

@Repository
public interface DeliveryMasterManagerLinkRepository extends PagingAndSortingRepository<DeliveryMasterManagerLinkT, Integer> {
	
	List<DeliveryMasterManagerLinkT> findByDeliveryManagerId(String managerId);
	
}
