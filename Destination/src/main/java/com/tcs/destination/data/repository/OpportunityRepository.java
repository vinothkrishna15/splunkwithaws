package com.tcs.destination.data.repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityT;

@Repository
public interface OpportunityRepository extends
		CrudRepository<OpportunityT, String> {

	List<OpportunityT> findByOpportunityNameIgnoreCaseLike(String opportunityname);

	List<OpportunityT> findByCustomerIdAndOpportunityRequestReceiveDateAfter(
			String customerId, Date fromDate);

	List<OpportunityT> findByOpportunityOwner(String primaryOwner);

	OpportunityT findByOpportunityId(String opportunityId);

	@Query(value = "select * from opportunity_t where opportunity_id in"
			+ " (select distinct opportunity_id from collaboration_comment_t where opportunity_id in"
			+ " (select opportunity_id from collaboration_comment_t "
			+ "where opportunity_id!='' and " + "user_id!=?1 "
			+ "order by updated_datetime Desc))", nativeQuery = true)
	List<OpportunityT> findTrendingOpportunities(String userId);

	@Query(value = "select digital_deal_value,deal_currency from opportunity_t where opportunity_owner=?1 and deal_closure_date >= ?2 and deal_closure_date <= ?3 and sales_stage_code =9", nativeQuery = true)
	List<Object[]> findDealValueForWins(String userId, Date fromDate,Date toDate);

	@Query(value = "select digital_deal_value,deal_currency from opportunity_t opp where opp.opportunity_owner=?1 and opp.onhold='NO' and  opp.opportunity_id in (select opportunity_id from (select opportunity_id, max(updated_datetime) from opportunity_timeline_history_t where updated_datetime <= ?2 and sales_stage_code < 9 group by opportunity_id order by opportunity_id) as opp_pipeline)", nativeQuery = true)
	List<Object[]> findDealValueForPipeline(String userId,Timestamp endTime);

	@Query(value = "select * from opportunity_t where opportunity_id IN (select opportunity_id from bid_details_t where target_bid_submission_date BETWEEN ?2 AND ?3) AND opportunity_owner = ?1", nativeQuery = true)
	List<OpportunityT> findByOpportunityOwnerAndDealClosureDateBetween(String userId, Date fromDate, Date toDate);
	
	
	@Query(value="select * from opportunity_t where opportunity_id IN (select opportunity_id from bid_details_t where target_bid_submission_date BETWEEN ?2 AND ?3 AND  bid_id in( select bid_id from bid_office_group_owner_link_t where bid_office_group_owner= ?1))", nativeQuery=true)
	List<OpportunityT> findOpportunityTFromBidDetailsTFromBidOfficeGroupOwnerLinkTByUserId(String userId,Date fromDate,Date toDate);
	
	@Query(value="select * from opportunity_t where opportunity_id IN (select opportunity_id from bid_details_t where target_bid_submission_date BETWEEN ?2 AND ?3) AND opportunity_id IN (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner=?1)", nativeQuery=true)
	List<OpportunityT> findOpportunityTForSalesSupportOwnerWithDateBetween(String userId, Date fromDate,
			Date toDate);

}