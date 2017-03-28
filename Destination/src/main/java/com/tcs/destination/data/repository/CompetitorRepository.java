package com.tcs.destination.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.CompetitorMappingT;

@Repository
public interface CompetitorRepository extends
		CrudRepository<CompetitorMappingT, String> {

	 List<CompetitorMappingT> findByActiveTrueAndCompetitorNameIgnoreCaseLike(String name);
	 
	 Page<CompetitorMappingT> findByCompetitorNameIgnoreCaseLike(String string, Pageable pageable);
		
	 @Query(value="select competitor_name from competitor_mapping_t", nativeQuery=true)
	 List<String> getCompetitorName();

	CompetitorMappingT findByActiveTrueAndCompetitorName(String competitorName);
	
	List<CompetitorMappingT> findByCompetitorNameIgnoreCaseLike(String name);
	
	 @Query(value="SELECT DISTINCT cmt FROM CompetitorMappingT cmt "
	 		+ "JOIN cmt.opportunityCompetitorLinkTs oclt "
	 		+ "JOIN oclt.opportunityT ot "
	 		+ "WHERE (ot.salesStageCode in (4,5,6,7,8) "
	 		+ "OR (ot.salesStageCode in (9,10) AND ot.dealClosureDate between :startDate and :endDate))"
	 		+ "AND (cmt.competitorName in (:competitors))")
	Page<CompetitorMappingT> findByNameContainingAndDealDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("competitors") List<String> competitors, Pageable pageable);

	 @Query(value="SELECT DISTINCT cmt FROM CompetitorMappingT cmt "
			 + "JOIN cmt.opportunityCompetitorLinkTs oclt "
			 + "JOIN oclt.opportunityT ot "
			 + "WHERE (ot.salesStageCode in (4,5,6,7,8) "
			 + "OR (ot.salesStageCode in (9,10) AND ot.dealClosureDate between :startDate and :endDate))")
	 Page<CompetitorMappingT> findByNameContainingAndDealDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
	 
	 
	 @Query(value="select cmt1.competitor_name, cmt1.logo, wins.win_count, wins.win_value, loss.loss_count, loss.loss_value, pipe.pipe_count, pipe.pipe_value from competitor_mapping_t cmt1"
	 		+ " JOIN (select cmtwin.competitor_name, sum(deal_value_usd_converter(ot.digital_deal_value, ot.deal_currency)) as win_value, count(ot) as win_count from competitor_mapping_t cmtwin"
	 		+ " LEFT JOIN opportunity_competitor_link_t oclt on oclt.competitor_name = cmtwin.competitor_name"
	 		+ " LEFT JOIN opportunity_t ot on oclt.opportunity_id = ot.opportunity_id AND ot.sales_stage_code in (9) AND ot.deal_closure_date between :startDate and :endDate"
	 		+ " group by cmtwin.competitor_name) as wins on wins.competitor_name = cmt1.competitor_name"
	 		+ " JOIN (select cmtloss.competitor_name, sum(deal_value_usd_converter(ot.digital_deal_value, ot.deal_currency)) as loss_value, count(ot) as loss_count from competitor_mapping_t cmtloss"
	 		+ " LEFT JOIN opportunity_competitor_link_t oclt on oclt.competitor_name = cmtloss.competitor_name"
	 		+ " LEFT JOIN opportunity_t ot on oclt.opportunity_id = ot.opportunity_id AND ot.sales_stage_code in (10) AND ot.deal_closure_date between :startDate and :endDate"
	 		+ " group by cmtloss.competitor_name) as loss on loss.competitor_name = cmt1.competitor_name"
	 		+ " JOIN (select cmtpipe.competitor_name, sum(deal_value_usd_converter(ot.digital_deal_value, ot.deal_currency)) as pipe_value, count(ot) as pipe_count from competitor_mapping_t cmtpipe"
	 		+ " LEFT JOIN opportunity_competitor_link_t oclt on oclt.competitor_name = cmtpipe.competitor_name"
	 		+ " LEFT JOIN opportunity_t ot on oclt.opportunity_id = ot.opportunity_id AND ot.sales_stage_code in (4,5,6,7,8)"
	 		+ " group by cmtpipe.competitor_name) as pipe on pipe.competitor_name = cmt1.competitor_name"
	 		+ " where cmt1.competitor_name in (:competitors) OR ('') in (:competitors)"
	 		+ "order by wins.win_count DESC"
	 		, nativeQuery = true)
	 List<Object[]> findOpportunityMetrics(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("competitors") List<String> competitors);
	 
	@Query(value = "SELECT cmt.logo FROM CompetitorMappingT cmt WHERE cmt.competitorName=:id")
	byte[] getLogo(@Param("id") String id);



	
}
