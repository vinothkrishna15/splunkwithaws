package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryMasterT;

@Repository
public interface DeliveryMasterRepository extends CrudRepository<DeliveryMasterT, Integer> {

}
