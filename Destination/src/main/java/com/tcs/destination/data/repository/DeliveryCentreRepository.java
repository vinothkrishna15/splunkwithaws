package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryCentreT;

@Repository
public interface DeliveryCentreRepository extends
		CrudRepository<DeliveryCentreT, Integer> {

	
	DeliveryCentreT findByDeliveryCentreHead(String deliveryCentreHead);
	
	List<DeliveryCentreT> findByDeliveryClusterId(Integer deliveryClusterId);

	DeliveryCentreT findByDeliveryCentreId(Integer id);
	
	List<DeliveryCentreT> findByDeliveryCentreIdIn(List<Integer> deliveryCentreIds);
	
	@Query(value = "select deliveryCentreId from DeliveryCentreT")
	List<Integer> findAllDeliveryCentreIds();

	@Query(value = "select CET.deliveryCentreId from DeliveryCentreT CET "
			+ "JOIN CET.deliveryClusterT CLT "
			+ "WHERE CLT.deliveryClusterHead = :clusterHeadId OR CET.deliveryCentreId = -1")
	List<Integer> findAllCentreIdsOfCluster(@Param("clusterHeadId") String clusterHeadId);

	/**
	 * Retrieves all the delivery centres except open -> delivery centre id -1
	 * @param deliveryCentreId
	 * @return
	 */
	List<DeliveryCentreT> findByDeliveryCentreIdGreaterThanEqual(int deliveryCentreId);

	@Query(value = "select delivery_centre from delivery_centre_t where delivery_centre_id in (:deliveryCentres)", nativeQuery=true)
	List<String> findDeliveryCentreNamesByIds(@Param("deliveryCentres") List<Integer> deliveryCentres);
	
}
