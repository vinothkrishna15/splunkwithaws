package com.tcs.destination.data.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryCentreUtilizationT;

@Repository
public interface DeliveryCentreUtilizationRepository extends CrudRepository<DeliveryCentreUtilizationT, Integer> {

	List<DeliveryCentreUtilizationT> findByDateBetween(Date startDate,
			Date endDate);

	@Query(value = "select avg(utilizationPercentage) from DeliveryCentreUtilizationT"
			+ " where date between (:startDate) and (:endDate)"
			+ " and categoryId = (:categoryId)")
	BigDecimal getOverallPercentage(@Param("startDate") Date startDate,@Param("endDate") Date endDate,
			@Param("categoryId") int categoryId);

	List<DeliveryCentreUtilizationT> findByDateBetweenAndCategoryId(
			Date startDate, Date endDate, int categoryId);

}
