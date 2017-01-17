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

	@Query(value = "select sum(utilization_percentage) from delivery_centre_utilization_t "
			+ "where category_id = (:categoryId) group by date order by date DESC limit 1", nativeQuery = true)
	BigDecimal getOverallPercentage(@Param("categoryId") int categoryId);

	List<DeliveryCentreUtilizationT> findByDateBetweenAndCategoryId(
			Date startDate, Date endDate, int categoryId);

	List<DeliveryCentreUtilizationT> findByClusterIdAndOverallPercentageId(
			Integer deliveryClusterId, Integer overallPercentageId);

	List<DeliveryCentreUtilizationT> findBydeliveryCentreIdAndOverallPercentageId(
			Integer deliveryCentreId, Integer overallPercentageId);

}
