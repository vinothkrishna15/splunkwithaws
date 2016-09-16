package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryClusterT;

@Repository
public interface DeliveryClusterRepository extends CrudRepository<DeliveryClusterT, Integer> {

	DeliveryClusterT findByDeliveryClusterHead(String deliveryClusterHead);
	
}
