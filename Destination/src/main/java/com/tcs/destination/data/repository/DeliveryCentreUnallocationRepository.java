package com.tcs.destination.data.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryCentreUnallocationT;

@Repository
public interface DeliveryCentreUnallocationRepository extends CrudRepository<DeliveryCentreUnallocationT, Integer>{

	List<DeliveryCentreUnallocationT> findByDateBetween(Date startDate,
			Date endDate);

	List<DeliveryCentreUnallocationT> findByDeliveryCentreIdAndDateBetween(Integer deliveryCentreId, Date startDate,
			Date endDate);

	@Query(value="select sum(COALESCE(trainee_percentage,0) + COALESCE(senior_percentage,0) + COALESCE(junior_percentage,0))"
			+ " from delivery_centre_unallocation_t group by date order by date DESC limit 1", nativeQuery = true)
	BigDecimal getOverallPercentage();
	
}
