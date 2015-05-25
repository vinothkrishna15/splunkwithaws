package com.tcs.destination.data.repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityT;

@Repository
public interface OpportunityRepository extends
		CrudRepository<OpportunityT, String> {

	List<OpportunityT> findByOpportunityNameIgnoreCaseLike(
			String opportunityname);

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
	List<Object[]> findDealValueForWins(String userId, Date fromDate,
			Date toDate);

	@Query(value = "select digital_deal_value,deal_currency from opportunity_t opp where opp.opportunity_owner=?1 and opp.onhold='NO' and  opp.opportunity_id in (select opportunity_id from (select opportunity_id, max(updated_datetime) from opportunity_timeline_history_t where updated_datetime <= ?2 and sales_stage_code < 9 group by opportunity_id order by opportunity_id) as opp_pipeline)", nativeQuery = true)
	List<Object[]> findDealValueForPipeline(String userId, Timestamp endTime);

	@Query(value = "select * from opportunity_t where opportunity_id IN (select opportunity_id from bid_details_t where target_bid_submission_date BETWEEN ?2 AND ?3) AND opportunity_owner = ?1", nativeQuery = true)
	List<OpportunityT> findByOpportunityOwnerAndDealClosureDateBetween(
			String userId, Date fromDate, Date toDate);

	@Query(value = "select * from opportunity_t where opportunity_id IN (select opportunity_id from bid_details_t where target_bid_submission_date BETWEEN ?2 AND ?3 AND  bid_id in( select bid_id from bid_office_group_owner_link_t where bid_office_group_owner= ?1))", nativeQuery = true)
	List<OpportunityT> findOpportunityTFromBidDetailsTFromBidOfficeGroupOwnerLinkTByUserId(
			String userId, Date fromDate, Date toDate);

	@Query(value = "select * from opportunity_t where opportunity_id IN (select opportunity_id from bid_details_t where target_bid_submission_date BETWEEN ?2 AND ?3) AND opportunity_id IN (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner=?1)", nativeQuery = true)
	List<OpportunityT> findOpportunityTForSalesSupportOwnerWithDateBetween(
			String userId, Date fromDate, Date toDate);

	@Query(value = "select count(*) as Bids, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / "
			+ "(select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as OBV from opportunity_t OPP "
			+ "JOIN bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "JOIN opportunity_timeline_history_t OTH ON (OTH.opportunity_id = OPP.opportunity_id and OTH.sales_stage_code between 4 and 8 and OTH.updated_datetime between (:fromDate) and (:toDate)) "
			+ "where OPP.digital_deal_value <> 0", nativeQuery = true)
	List<Object[]> findPipelinePerformance(
			@Param("geography") String geography, @Param("iou") String iou,
			@Param("serviceLine") String serviceLine,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select count(*) as Wins, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as OBV, avg((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Mean, median((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Median from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and "
			+ "(ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "JOIN opportunity_timeline_history_t OTH ON (OTH.opportunity_id = OPP.opportunity_id and OTH.sales_stage_code = 9	and OTH.updated_datetime between (:fromDate) and (:toDate)) "
			+ "where OPP.digital_deal_value <> 0", nativeQuery = true)
	List<Object[]> findWinsPerformance(@Param("geography") String geography,
			@Param("iou") String iou, @Param("serviceLine") String serviceLine,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select * from opportunity_t OPP where OPP.opportunity_id in ( "
			+ "(select opportunity_id from opportunity_t where opportunity_owner = ?1) union "
			+ "(select opportunity_id from opportunity_sales_support_link_t where sales_support_owner = ?2) union "
			+ "(select opportunity_id from bid_details_t BDT where BDT.bid_id in "
			+ "(select bid_id from bid_office_group_owner_link_t where bid_office_group_owner= ?3))) "
			+ "order by OPP.created_modified_datetime desc", nativeQuery = true) 
	List<OpportunityT> findOpportunityTsByOwnerAndRole(String primaryOwner, String salesSupportOwner, String bidOfficeOwner);
}