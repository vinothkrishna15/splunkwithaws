package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryMasterT;

@Repository
public interface DeliveryMasterPagingRepository extends PagingAndSortingRepository<DeliveryMasterT, Integer> {

	
	
	Page<DeliveryMasterT> findByDeliveryManagerIdAndDeliveryStageIn(String userId,List<Integer>deliveryStages,Pageable pageable);
	Page<DeliveryMasterT> findByDeliveryCentreIdAndDeliveryStageIn(Integer deliveryCentreId,List<Integer> deliveryStages,Pageable pageable);
	Page<DeliveryMasterT> findByDeliveryCentreIdInAndDeliveryStageIn(List<Integer> deliveryCentreIds,List<Integer> deliveryStages,Pageable pageable);
	//Page<DeliveryMasterT> findByUserIdAndEntityTypeIgnoreCaseOrderByCreatedDatetimeDesc(String userId, String entityType, Pageable pageable);
	
	
	//for manager
	//order by deliveryMasterId
//	Page<DeliveryMasterT> findByDeliveryManagerIdAndDeliveryStageInOrderByDeliveryMasterId (String deliveryManagerId, List<Integer> deliveryStages,Pageable pageable);
//	Page<DeliveryMasterT> findByDeliveryManagerIdAndDeliveryStageInOrderByDeliveryMasterIdDesc(String deliveryManagerId, List<Integer> deliveryStages, Pageable pageable);
//	
//	//order by opportunityId
//	Page<DeliveryMasterT> findByDeliveryManagerIdAndDeliveryStageInOrderByOpportunityId (String deliveryManagerId,List<Integer> deliveryStages, Pageable pageable);
//	Page<DeliveryMasterT> findByDeliveryManagerIdAndDeliveryStageInOrderByOpportunityIdDesc(String deliveryManagerId,List<Integer> deliveryStages, Pageable pageable);
//	
//	//order by deliveryCentreId
//	Page<DeliveryMasterT> findByDeliveryManagerIdAndDeliveryStageInOrderByDeliveryCentreId (String deliveryManagerId,List<Integer> deliveryStages, Pageable pageable);
//	Page<DeliveryMasterT> findByDeliveryManagerIdAndDeliveryStageInOrderByDeliveryCentreIdDesc(String deliveryManagerId,List<Integer> deliveryStages, Pageable pageable);
//	
//	
//	
//	
//	//for centre head
//	//order by deliveryMasterId
//	Page<DeliveryMasterT> findByDeliveryCentreIdAndDeliveryStageInOrderByDeliveryMasterId(Integer deliveryCentreId,List<Integer> deliveryStages,Pageable pageable);
//	Page<DeliveryMasterT> findByDeliveryCentreIdAndDeliveryStageInOrderByDeliveryMasterIdDesc(Integer deliveryCentreId,List<Integer> deliveryStages,Pageable pageable);
//	
//	//order by opportunityId
//	Page<DeliveryMasterT> findByDeliveryCentreIdAndDeliveryStageInOrderByOpportunityId(Integer deliveryCentreId,List<Integer> deliveryStages,Pageable pageable);
//	Page<DeliveryMasterT> findByDeliveryCentreIdAndDeliveryStageInOrderByOpportunityIdDesc(Integer deliveryCentreId,List<Integer> deliveryStages,Pageable pageable);
//	
//	//order by deliveryCentreId
//	Page<DeliveryMasterT> findByDeliveryCentreIdAndDeliveryStageInOrderByDeliveryCentreId(Integer deliveryCentreId,List<Integer> deliveryStages,Pageable pageable);
//	Page<DeliveryMasterT> findByDeliveryCentreIdAndDeliveryStageInOrderByDeliveryCentreIdDesc(Integer deliveryCentreId,List<Integer> deliveryStages,Pageable pageable);
//
//	
//	
//	
//	//for cluster head
//	//order by deliveryMasterId
//	Page<DeliveryMasterT> findByDeliveryCentreIdInAndDeliveryStageInOrderByDeliveryMasterId(List<Integer> deliveryCentreIds,List<Integer> deliveryStages,Pageable pageable);
//	Page<DeliveryMasterT> findByDeliveryCentreIdInAndDeliveryStageInOrderByDeliveryMasterIdDesc(List<Integer> deliveryCentreIds,List<Integer> deliveryStages,Pageable pageable);
//		
//	//order by opportunityId
//	Page<DeliveryMasterT> findByDeliveryCentreIdInAndDeliveryStageInOrderByOpportunityId(List<Integer> deliveryCentreIds,List<Integer> deliveryStages,Pageable pageable);
//	Page<DeliveryMasterT> findByDeliveryCentreIdInAndDeliveryStageInOrderByOpportunityIdDesc(List<Integer> deliveryCentreIds,List<Integer> deliveryStages,Pageable pageable);
//		
//	//order by deliveryCentreId
//	Page<DeliveryMasterT> findByDeliveryCentreIdInAndDeliveryStageInOrderByDeliveryCentreId(List<Integer> deliveryCentreIds,List<Integer> deliveryStages,Pageable pageable);
//	Page<DeliveryMasterT> findByDeliveryCentreIdInAndDeliveryStageInOrderByDeliveryCentreIdDesc(List<Integer> deliveryCentreIds,List<Integer> deliveryStages,Pageable pageable);
//
//	
	
}
