package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.DeliveryOwnershipT;

public interface DeliveryOwnershipRepository extends
		CrudRepository<DeliveryOwnershipT, String> {

}
