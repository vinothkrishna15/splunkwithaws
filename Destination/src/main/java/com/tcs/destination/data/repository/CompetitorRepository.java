package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.CompetitorMappingT;

@Repository
public interface CompetitorRepository extends
		CrudRepository<CompetitorMappingT, String> {

	 List<CompetitorMappingT> findByActiveTrueAndCompetitorNameIgnoreCaseLike(String name);

	 @Query(value="select competitor_name from competitor_mapping_t", nativeQuery=true)
	 List<String> getCompetitorName();

	CompetitorMappingT findByActiveTrueAndCompetitorName(String competitorName);
	
	List<CompetitorMappingT> findByCompetitorNameIgnoreCaseLike(String name);

}
