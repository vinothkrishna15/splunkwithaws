package com.tcs.destination.data.repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityT;

@Repository
public interface OpportunityRepository extends
		JpaRepository<OpportunityT, String> {

	List<OpportunityT> findByOpportunityIdInOrderByCountryAsc(
			List<String> opportunityId);

	List<OpportunityT> findByOpportunityNameIgnoreCaseLike(
			String opportunityname);

	List<OpportunityT> findByOpportunityNameIgnoreCaseLikeAndCustomerId(
			String opportunityname, String customerId);

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

	@Query(value = "select digital_deal_value,deal_currency from opportunity_t opp where opp.opportunity_owner=?1 and  opp.opportunity_id in (select opportunity_id from (select opportunity_id, max(updated_datetime) from opportunity_timeline_history_t where updated_datetime <= ?2 and sales_stage_code < 9 group by opportunity_id order by opportunity_id) as opp_pipeline)", nativeQuery = true)
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
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code=9 and OPP.deal_closure_date between (:fromDate) and (:toDate)", nativeQuery = true)
	List<Object[]> findWinsPerformance(@Param("geography") String geography,
			@Param("iou") String iou, @Param("serviceLine") String serviceLine,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select * from opportunity_t OPP where OPP.opportunity_id in ( "
			+ "(select opportunity_id from opportunity_t where opportunity_owner = ?1) union "
			+ "(select opportunity_id from opportunity_sales_support_link_t where sales_support_owner = ?2) union "
			+ "(select opportunity_id from bid_details_t BDT where BDT.bid_id in "
			+ "(select bid_id from bid_office_group_owner_link_t where bid_office_group_owner= ?3))) "
			+ "order by OPP.modified_datetime desc", nativeQuery = true)
	List<OpportunityT> findOpportunityTsByOwnerAndRole(String primaryOwner,
			String salesSupportOwner, String bidOfficeOwner);

	@Query(value = "select OTH.sales_stage_code as SalesStage, count(*) as Bids, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "JOIN opportunity_timeline_history_t OTH ON (OTH.opportunity_id = OPP.opportunity_id and OTH.sales_stage_code between 4 and 8 and OTH.updated_datetime between (:fromDate) and (:toDate)) "
			+ "where OPP.digital_deal_value <> 0 group by SalesStage order by SalesStage", nativeQuery = true)
	List<Object[]> findPipelinePerformanceBySalesStage(
			@Param("geography") String geography, @Param("iou") String iou,
			@Param("serviceLine") String serviceLine,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select ICMT.display_iou, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
			+ "JOIN opportunity_timeline_history_t OTH ON (OTH.opportunity_id = OPP.opportunity_id and OTH.sales_stage_code between 4 and 8 and OTH.updated_datetime between (:fromDate) and (:toDate)) "
			+ "where OPP.digital_deal_value <> 0 group by ICMT.display_iou order by ICMT.display_iou", nativeQuery = true)
	List<Object[]> findPipelinePerformanceByIOU(
			@Param("geography") String geography,
			@Param("serviceLine") String serviceLine,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select ICMT.display_iou, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as OBV, avg((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Mean, median((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Median from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code=9 and OPP.deal_closure_date between (:fromDate) and (:toDate) group by ICMT.display_iou order by ICMT.display_iou", nativeQuery = true)
	List<Object[]> findWinsPerformanceByIOU(
			@Param("geography") String geography,
			@Param("serviceLine") String serviceLine,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select SSMT.display_sub_sp, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "JOIN opportunity_timeline_history_t OTH ON (OTH.opportunity_id = OPP.opportunity_id and OTH.sales_stage_code between 4 and 8 and OTH.updated_datetime between (:fromDate) and (:toDate)) "
			+ "where OPP.digital_deal_value <> 0 group by SSMT.display_sub_sp order by SSMT.display_sub_sp", nativeQuery = true)
	List<Object[]> findPipelinePerformanceByServiceLine(
			@Param("geography") String geography, @Param("iou") String iou,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select SSMT.display_sub_sp, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as OBV, avg((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = "
			+ "(:currency))) as Mean, median((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Median from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and "
			+ "(ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code=9 and OPP.deal_closure_date between (:fromDate) and (:toDate) group by SSMT.display_sub_sp order by SSMT.display_sub_sp", nativeQuery = true)
	List<Object[]> findWinsPerformanceByServiceLine(
			@Param("geography") String geography, @Param("iou") String iou,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select GMT.display_geography, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "JOIN opportunity_timeline_history_t OTH ON (OTH.opportunity_id = OPP.opportunity_id and OTH.sales_stage_code between 4 and 8 and OTH.updated_datetime between (:fromDate) and (:toDate)) "
			+ "where OPP.digital_deal_value <> 0 group by GMT.display_geography order by GMT.display_geography", nativeQuery = true)
	List<Object[]> findPipelinePerformanceByGeography(
			@Param("serviceLine") String serviceLine, @Param("iou") String iou,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select GMT.display_geography, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as OBV, avg((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = "
			+ "(:currency))) as Mean, median((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Median from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and "
			+ "(ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code=9 and OPP.deal_closure_date between (:fromDate) and (:toDate) group by GMT.display_geography order by GMT.display_geography", nativeQuery = true)
	List<Object[]> findWinsPerformanceByGeography(
			@Param("serviceLine") String serviceLine, @Param("iou") String iou,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select GMT.geography, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '')"
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id and (CMT.customer_name= (:customerName) OR (:customerName)='')"
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "JOIN opportunity_timeline_history_t OTH ON (OTH.opportunity_id = OPP.opportunity_id and OTH.sales_stage_code between 4 and 8 and OTH.updated_datetime between (:fromDate) and (:toDate)) "
			+ "where OPP.digital_deal_value <> 0 group by GMT.geography order by GMT.geography", nativeQuery = true)
	List<Object[]> findPipelinePerformanceBySubGeography(
			@Param("customerName") String customerName,
			@Param("serviceLine") String serviceLine, @Param("iou") String iou,
			@Param("geography") String geography,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select GMT.geography, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '')"
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id and (CMT.customer_name= (:customerName) OR (:customerName)='')"
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code=9 and OPP.deal_closure_date between (:fromDate) and (:toDate) group by GMT.geography order by GMT.geography", nativeQuery = true)
	List<Object[]> findWinsPerformanceBySubGeography(
			@Param("customerName") String customerName,
			@Param("serviceLine") String serviceLine, @Param("iou") String iou,
			@Param("geography") String geography,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	// list of top opportunities based on digital deal value
	@Query(value = "select distinct OPP.* from opportunity_t OPP"
			+ " JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id"
			+ " JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = ?2 OR ?2 = '')"
			+ " JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country"
			+ " JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = ?1 OR ?1 = '')"
			+ " JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id"
			+ " JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = ?3 OR ?3 = '')"
			+ " JOIN opportunity_timeline_history_t OTH"
			+ " 	ON (OTH.opportunity_id = OPP.opportunity_id and OTH.sales_stage_code between ?6 and ?7"
			+ " 	and OTH.updated_datetime between ?4 and ?5)"
			+ " where OPP.digital_deal_value <> 0"
			+ " order by OPP.digital_deal_value DESC limit ?8", nativeQuery = true)
	public List<OpportunityT> getTopOpportunities(String geography,
			String subSp, String iou, Date dateFrom, Date dateTo,
			int stageFrom, int stageTo, int count);

	public List<OpportunityT> findBySalesStageCode(int salesStageCode);

	/**
	 * This method retrieves deal values of opportunities of users
	 * 
	 * @param users
	 * @return
	 */
	@Query(value = "select sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) "
			+ "/ (select conversion_rate from beacon_convertor_mapping_t where currency_name = 'INR')),OPP.sales_stage_code,count(*),SSM.sales_stage_description "
			+ "from opportunity_t OPP JOIN sales_stage_mapping_t SSM ON OPP.sales_stage_code=SSM.sales_stage_code "
			+ "where OPP.opportunity_id in ((select opportunity_id from opportunity_t where opportunity_owner in (:users)) "
			+ "union (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (:users)) "
			+ "union (select opportunity_id from bid_details_t BDT where BDT.bid_id in (select bid_id from bid_office_group_owner_link_t "
			+ "where bid_office_group_owner in (:users)))) group by OPP.sales_stage_code,SSM.sales_stage_code", nativeQuery = true)
	public List<Object[]> findDealValueOfOpportunitiesBySupervisorId(
			@Param("users") List<String> users);

	List<OpportunityT> findBySalesStageCodeAndCustomerId(int salesStageCode,
			String customerId);

	/**
	 * This query retrieves pipeline deal value of all users under a supervisor
	 * 
	 * @param users
	 * @param endTime
	 * @return
	 */
	@Query(value = "select digital_deal_value,deal_currency from opportunity_t opp where opp.opportunity_owner in (:users) "
			+ "and  opp.opportunity_id in (select opportunity_id from (select opportunity_id, max(updated_datetime) from opportunity_timeline_history_t "
			+ "where updated_datetime <= (:endTime) and sales_stage_code < 9 group by opportunity_id order by opportunity_id) as opp_pipeline)", nativeQuery = true)
	List<Object[]> findDealValueForPipelineBySubordinatesPerSupervisor(
			@Param("users") List<String> users,
			@Param("endTime") Timestamp endTime);

	/**
	 * This query retrieves all the won deals of all users under a supervisor
	 * 
	 * @param users
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	@Query(value = "select digital_deal_value,deal_currency from opportunity_t where opportunity_owner in (:users) "
			+ "and (deal_closure_date between (:fromDate) and (:toDate)) and sales_stage_code =9", nativeQuery = true)
	List<Object[]> findDealValueForWinsBySubordinatesPerSupervisor(
			@Param("users") List<String> users,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	/**
	 * This query retrieves the opportunities for the users in the tables
	 * opportunity_t, opportunity_sales_support_link_t and bid_details_t
	 * 
	 * @param users
	 * @return
	 */
	@Query(value = "select * from opportunity_t OPP WHERE OPP.opportunity_id in ((select opportunity_id from opportunity_t where opportunity_owner in (:users)) "
			+ "union (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (:users)) "
			+ "union (select opportunity_id from bid_details_t BDT where BDT.bid_id in "
			+ "(select bid_id from bid_office_group_owner_link_t where bid_office_group_owner in (:users)))) order by OPP.created_datetime desc", nativeQuery = true)
	List<OpportunityT> findTeamOpportunityDetailsBySupervisorId(
			@Param("users") List<String> users);

	@Query(value = "select distinct(OPP.*) from opportunity_t OPP,customer_master_t CMT "
			+ "where (OPP.customer_id in (:customerIdList) or ('') in (:customerIdList)) "
			+ "and (OPP.sales_stage_code in (:salesStageCode) or (-1) in (:salesStageCode)) "
			+ "and (OPP.strategic_initiative=(:strategicInitiative) or  (:strategicInitiative)='' ) "
			+ "and (OPP.new_logo = (:newLogo) or (:newLogo) ='' ) "
			+ "and (((OPP.digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:dealCurrency)) between (:minDigitalDealValue) and (:maxDigitalDealValue) ) or (:defaultDealRange)='YES') "
			+ "and (OPP.digital_flag=(:digitalFlag) or (:digitalFlag)='' ) "
			+ "and (CMT.iou in (select iou from iou_customer_mapping_t where display_iou in (:displayIou)) or ('') in (:displayIou)) "
			+ "and (OPP.country in (:country) or ('') in (:country)) "
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_partner_link_t where partner_id in (:partnerId)) or ('') in (:partnerId))"
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_competitor_link_t where competitor_name in (:competitorName)) or ('') in (:competitorName))"
			+ "and (OPP.opportunity_id in (select entity_id from search_keywords_t where UPPER(search_keywords) similar to (:searchKeywords)) or (:searchKeywords)='') "
			+ "and (OPP.opportunity_id in (select opportunity_id from bid_details_t where bid_request_type in (:bidRequestType)) or ('') in (:bidRequestType))"
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_offering_link_t where offering in (:offering)) or ('') in (:offering)) "
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_sub_sp_link_t where sub_sp in "
			+ "(select sub_sp from sub_sp_mapping_t where display_sub_sp in (:displaySubSp))) or ('') in (:displaySubSp)) "
			+ "and (UPPER(OPP.opportunity_name) similar to (:opportunityName) or (:opportunityName) = '') "
			+ "and (OPP.opportunity_owner in (:userId) or OPP.opportunity_id in "
			+ "(select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (:userId)) "
			+ "or OPP.opportunity_id in (select BDT.opportunity_id from bid_details_t BDT where BDT.bid_id in "
			+ "(select bid_id from bid_office_group_owner_link_t where bid_office_group_owner in (:userId))) or ('') in (:userId))"
			+ "and CMT.customer_id=OPP.customer_id", nativeQuery = true)
	List<OpportunityT> findByOpportunitiesIgnoreCaseLike(
			@Param("customerIdList") List<String> customerIdList,
			@Param("salesStageCode") List<Integer> salesStageCode,
			@Param("strategicInitiative") String strategicInitiative,
			@Param("newLogo") String newLogo,
			@Param("defaultDealRange") String defaultDealRange,
			@Param("minDigitalDealValue") double minDigitalDealValue,
			@Param("maxDigitalDealValue") double maxDigitalDealValue,
			@Param("dealCurrency") String dealCurrency,
			@Param("digitalFlag") String digitalFlag,
			@Param("displayIou") List<String> displayIou,
			@Param("country") List<String> country,
			@Param("partnerId") List<String> partnerId,
			@Param("competitorName") List<String> competitorName,
			@Param("searchKeywords") String searchKeywords,
			@Param("bidRequestType") List<String> bidRequestType,
			@Param("offering") List<String> offering,
			@Param("displaySubSp") List<String> displaySubSp,
			@Param("opportunityName") String opportunityName,
			@Param("userId") List<String> userId);

	/**
	 * This query returns the sum of digital deal values for the given list of
	 * opportunity Ids
	 * 
	 * @param opportunityId
	 * @return Sum of digital deal values - Integer
	 */
	@Query(value="select sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = 'USD')) from opportunity_t OPP where OPP.opportunity_id in (:opportunityId)", nativeQuery = true)
	Double findDigitalDealValueByOpportunityIdIn(@Param("opportunityId") List<String> opportunityId);

}