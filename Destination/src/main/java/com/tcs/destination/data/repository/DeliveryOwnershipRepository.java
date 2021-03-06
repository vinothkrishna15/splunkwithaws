package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryOwnershipT;

@Repository
public interface DeliveryOwnershipRepository extends
		CrudRepository<DeliveryOwnershipT, Integer> {

}
