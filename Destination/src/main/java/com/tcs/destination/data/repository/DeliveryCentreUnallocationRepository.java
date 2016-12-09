package com.tcs.destination.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryCentreUnallocationT;

@Repository
public interface DeliveryCentreUnallocationRepository extends CrudRepository<DeliveryCentreUnallocationT, Integer>{

	List<DeliveryCentreUnallocationT> findByDateBetween(Date startDate,
			Date endDate);
	
}
