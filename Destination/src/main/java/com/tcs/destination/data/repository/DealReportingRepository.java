/**
 * 
 * DealReportingRepository.java 
 *
 * @author TCS
 * @Version 1.0 - 2016
 * 
 * @Copyright 2016 Tata Consultancy 
 */
package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DealClosureReportingT;

/**
 * This DealReportingRepository holds the database services for the entity DealClosureReportingT
 * 
 */
@Repository
public interface DealReportingRepository extends CrudRepository<DealClosureReportingT, Integer>{
	
	@Query("update DealClosureReportingT d set d.active = ?1 where d.active = 'true'") 
	void updateDealClosureActiveStatus(Boolean active);
	
	@Query("select d from DealClosureReportingT d where d.active = ?1 order by d.dealReportingStartDate") 
	List<DealClosureReportingT> findByActiveOrderByDealReportingStartDate(Boolean active);
	
}
