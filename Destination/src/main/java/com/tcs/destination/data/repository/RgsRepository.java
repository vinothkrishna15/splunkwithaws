package com.tcs.destination.data.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryRgsT;


/**
 * 
 * Repository for working with {@link DeliveryRgsT} domain objects
 */
@Repository
public interface RgsRepository extends CrudRepository<DeliveryRgsT, String> {
	
	   
}
