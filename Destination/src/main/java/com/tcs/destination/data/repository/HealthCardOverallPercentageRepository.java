package com.tcs.destination.data.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.HealthCardOverallPercentage;

@Repository
public interface HealthCardOverallPercentageRepository extends CrudRepository<HealthCardOverallPercentage, Integer>{

	List<HealthCardOverallPercentage> findByDateBetweenAndComponentIdOrderByDateDesc(
			Date startDate, Date endDate, int categoryId);

	@Query(value = "select overall_percentage from health_card_overall_percentage "
			+ "where component_id = (:componentId) order by date DESC limit 1",nativeQuery=true)
	BigDecimal getOverallPercentage(@Param("componentId") int componentId);

	@Query(value = "select  overall_percentage_id, date from health_card_overall_percentage ",nativeQuery=true)
	List<Object[]> getOverallPercentageIdAndDate();

}
