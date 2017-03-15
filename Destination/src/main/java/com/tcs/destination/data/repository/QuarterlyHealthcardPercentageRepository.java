package com.tcs.destination.data.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.QuarterlyHealthcardPercentage;

@Repository
public interface QuarterlyHealthcardPercentageRepository extends CrudRepository<QuarterlyHealthcardPercentage, Integer> {

	@Query(value="select avg(percentage) from (select percentage from "
			+ "quarterly_healthcard_percentage order by modified_datetime "
			+ "DESC limit 4) as master",nativeQuery=true )
	BigDecimal getQuarterlyAveragePercentage();

}
