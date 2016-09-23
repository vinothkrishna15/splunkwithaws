package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryCentreT;

@Repository
public interface DeliveryCentreRepository extends
		CrudRepository<DeliveryCentreT, Integer> {

	
	DeliveryCentreT findByDeliveryCentreHead(String deliveryCentreHead);
	
	List<DeliveryCentreT> findByDeliveryClusterId(Integer deliveryClusterId);

	DeliveryCentreT findByDeliveryCentreId(Integer id);
	
}
