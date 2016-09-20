package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryMasterT;

@Repository
public interface DeliveryMasterPagingRepository extends PagingAndSortingRepository<DeliveryMasterT, Integer> {
	//Page<DeliveryMasterT> findByDeliveryManagerIdAndDeliveryStageIn(String userId,List<Integer> deliveryStages,Pageable pageable);
	Page<DeliveryMasterT> findByDeliveryMasterIdInAndDeliveryStageIn(List<Integer> deliveryMasterIds,List<Integer> deliveryStages,Pageable pageable);
	//Page<DeliveryMasterT> findByDeliveryCentreIdInAndDeliveryStageIn(List<Integer> deliveryCentreIds,List<Integer> deliveryStages,Pageable pageable);
	Page<DeliveryMasterT> findByDeliveryCentreIdInAndDeliveryStageIn(List<Integer> deliveryCentreIds,List<Integer> deliveryStages,Pageable pageable);
}
