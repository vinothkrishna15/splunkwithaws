package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryMasterT;

@Repository
public interface DeliveryMasterRepository extends JpaRepository<DeliveryMasterT, Integer> {
	
}
