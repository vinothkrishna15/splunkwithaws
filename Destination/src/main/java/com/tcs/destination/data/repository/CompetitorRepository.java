package com.tcs.destination.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

	//TODO sdhfjsh
	
	 @Query(value="SELECT DISTINCT cmt FROM CompetitorMappingT cmt "
	 		+ "JOIN cmt.opportunityCompetitorLinkTs oclt "
	 		+ "JOIN oclt.opportunityT ot "
	 		+ "WHERE (ot.salesStageCode in (4,5,6,7,8) "
	 		+ "OR (ot.salesStageCode in (9,10,11,13) AND ot.dealClosureDate between :startDate and :endDate)) "
	 		+ "AND cmt.competitorName LIKE :chars")
	List<CompetitorMappingT> findByNameContainingAndDealDate(@Param("chars") String chars,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate);
	 

}
