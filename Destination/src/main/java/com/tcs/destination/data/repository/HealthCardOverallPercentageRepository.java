package com.tcs.destination.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.HealthCardOverallPercentage;

@Repository
public interface HealthCardOverallPercentageRepository extends CrudRepository<HealthCardOverallPercentage, Integer>{

	List<HealthCardOverallPercentage> findByDateBetweenAndComponentIdOrderByDateDesc(
			Date startDate, Date endDate, int categoryId);

}
