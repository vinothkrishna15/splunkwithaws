package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.CompetitorMappingT;

@Repository
public interface CompetitorRepository extends
		CrudRepository<CompetitorMappingT, String> {

	 List<CompetitorMappingT> findByCompetitorNameIgnoreCaseLike(String name);

}
