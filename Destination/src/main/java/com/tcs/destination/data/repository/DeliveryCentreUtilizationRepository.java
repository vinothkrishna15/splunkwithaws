package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryCentreUtilizationT;

@Repository
public interface DeliveryCentreUtilizationRepository extends CrudRepository<DeliveryCentreUtilizationT, Integer> {

}
