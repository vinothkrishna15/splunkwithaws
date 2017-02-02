package com.tcs.destination.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryClusterT;

@Repository
public interface DeliveryClusterRepository extends CrudRepository<DeliveryClusterT, Integer> {

	DeliveryClusterT findByDeliveryClusterHead(String deliveryClusterHead);

	@Query(value = "SELECT distinct dct from DeliveryClusterT dct"
			+ " JOIN dct.deliveryCentreTs dcen"
			+ " JOIN dcen.unallocationTs ut"
			+ " WHERE ut.date BETWEEN :fromDate AND :toDate")
	List<DeliveryClusterT> findByUnAllocation( @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	@Query(value = "SELECT dct from DeliveryClusterT dct WHERE dct.deliveryClusterId > 0")
	List<DeliveryClusterT> findAllExceptOpen();
	
	@Query(value="select delivery_cluster_id, delivery_cluster from delivery_cluster_t  ",nativeQuery=true)
	List<Object[]> findDeliveryCluster();
}
