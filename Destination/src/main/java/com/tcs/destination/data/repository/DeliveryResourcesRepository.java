package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryCentreT;
import com.tcs.destination.bean.DeliveryResourcesT;

@Repository
public interface DeliveryResourcesRepository extends
		CrudRepository<DeliveryResourcesT, Integer> {
	
}
