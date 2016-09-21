package com.tcs.destination.data.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryRequirementT;

@Repository
public interface DeliveryRequirementRepository extends
		CrudRepository<DeliveryRequirementT, Integer> {
	
}
