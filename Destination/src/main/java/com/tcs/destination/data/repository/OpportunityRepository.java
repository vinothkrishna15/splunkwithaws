package com.tcs.destination.data.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityT;

@Repository
public interface OpportunityRepository extends
		JpaRepository<OpportunityT, String> {

	List<OpportunityT> findByOpportunityIdInOrderByCountryAsc(
			List<String> opportunityId);
	
	Page<OpportunityT> findByOpportunityNameIgnoreCaseLikeOrderByModifiedDatetimeDesc(
			String opportunityname, Pageable page);

	Page<OpportunityT> findByOpportunityNameIgnoreCaseLikeAndCustomerIdOrderByModifiedDatetimeDesc(
			String opportunityname, String customerId, Pageable pageable);

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
	/*
	 * The query gives the count of opportunity for a given customerID
	 */
	@Query(value = "select count(opportunity_id) from opportunity_t where customer_id=? and sales_stage_code<9", nativeQuery = true)
	int getOpportunityCountByCustomerId(String customerId);

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

	@Query(value = "select count(*) as Wins, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as OBV, avg((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Mean, median((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Median from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id and (CMT.customer_name in (:customer) OR ('') in (:customer)) "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and "
			+ "(ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code=9 and OPP.deal_closure_date between (:fromDate) and (:toDate)", nativeQuery = true)
	List<Object[]> findWinsPerformance(@Param("geography") String geography,
			@Param("iou") String iou, @Param("serviceLine") String serviceLine,
			@Param("currency") String currency,
			@Param("customer") List<String> customer,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select * from opportunity_t OPP where OPP.opportunity_id in ( "
			+ "(select opportunity_id from opportunity_t where opportunity_owner = ?1) union "
			+ "(select opportunity_id from opportunity_sales_support_link_t where sales_support_owner = ?2) union "
			+ "(select opportunity_id from bid_details_t BDT where BDT.bid_id in "
			+ "(select bid_id from bid_office_group_owner_link_t where bid_office_group_owner= ?3))) "
			+ "order by OPP.modified_datetime desc", nativeQuery = true)
	List<OpportunityT> findOpportunityTsByOwnerAndRole(String primaryOwner,
			String salesSupportOwner, String bidOfficeOwner);

	@Query(value = "select OPP.sales_stage_code as SalesStage, count(*) as oppCount, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV,"
			+ "median((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Median,"
			+ "avg((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Mean  from opportunity_t OPP "
			+ "JOIN bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:displayGeography) OR (:displayGeography) = '') and (GMT.geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id and (CMT.customer_name in (:customer) OR ('') in (:customer)) "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where ((OPP.sales_stage_code >= 9 and deal_closure_date between (:fromDate) and (:toDate)) or OPP.sales_stage_code < 9) "
			+ "and OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo) "
			+ "group by SalesStage order by SalesStage", nativeQuery = true)
	List<Object[]> findPipelinePerformanceBySalesStage(
			@Param("displayGeography") String displayGeography,
			@Param("geography") String geography, @Param("iou") String iou,
			@Param("serviceLine") String serviceLine,
			@Param("currency") String currency,
			@Param("customer") List<String> customer,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("salesStageFrom") int salesStageFrom,
			@Param("salesStageTo") int salesStageTo);

	@Query(value = "select ICMT.display_iou, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
			+ "where OPP.digital_deal_value <> 0 "
			+ "AND ((OPP.deal_closure_date between (:fromDate)  and (:toDate) and OPP.sales_stage_code >=9) or OPP.sales_stage_code < 9) "
			+ "AND (OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo)) group by ICMT.display_iou order by ICMT.display_iou", nativeQuery = true)
	List<Object[]> findPipelinePerformanceByIOU(
			@Param("geography") String geography,
			@Param("serviceLine") String serviceLine,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("salesStageFrom") int salesStageFrom,
			@Param("salesStageTo") int salesStageTo);

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

	@Query(value = "select SSMT.display_sub_sp, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / ((select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))*(select count(*) from opportunity_sub_sp_link_t where opportunity_id=OPP.opportunity_id)))  as OBV from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:displayGeography) OR (:displayGeography) = '') "
			+ "and (GMT.geography = (:geography) OR (:geography) = '') "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo)"
			+ "and ((OPP.deal_closure_date between (:fromDate)  and (:toDate) and OPP.sales_stage_code >=9) or OPP.sales_stage_code < 9) group by SSMT.display_sub_sp order by SSMT.display_sub_sp", nativeQuery = true)
	List<Object[]> findPipelinePerformanceByServiceLine(
			@Param("displayGeography") String displayGeography,
			@Param("geography") String geography, @Param("iou") String iou,
			@Param("currency") String currency,
			@Param("salesStageFrom") int salesStageFrom,
			@Param("salesStageTo") int salesStageTo,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select SSMT.display_sub_sp, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as OBV, avg((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = "
			+ "(:currency))) as Mean, median((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Median from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.geography = (:geography) OR (:geography) = '') "
			+ "and (GMT.display_geography=(:displayGeography) or (:displayGeography)='')"
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and "
			+ "(ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code=9 and OPP.deal_closure_date between (:fromDate) and (:toDate) group by SSMT.display_sub_sp order by SSMT.display_sub_sp", nativeQuery = true)
	List<Object[]> findWinsPerformanceByServiceLine(
			@Param("displayGeography") String displayGeography,
			@Param("geography") String geography, @Param("iou") String iou,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select GMT.display_geography, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo)"
			+ "and ((OPP.deal_closure_date between (:fromDate)  and (:toDate) and OPP.sales_stage_code >=9) or OPP.sales_stage_code < 9) group by GMT.display_geography order by GMT.display_geography", nativeQuery = true)
	List<Object[]> findPipelinePerformanceByGeography(
			@Param("serviceLine") String serviceLine, @Param("iou") String iou,
			@Param("currency") String currency,
			@Param("salesStageFrom") int salesStageFrom,
			@Param("salesStageTo") int salesStageTo,
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
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '')"
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id and (CMT.customer_name in (:customerName) OR ('') in (:customerName))"
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 "
			+ "AND ((OPP.deal_closure_date between (:fromDate)  and (:toDate) and OPP.sales_stage_code >=9) or OPP.sales_stage_code < 9) "
			+ "AND (OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo)) group by GMT.geography order by GMT.geography", nativeQuery = true)
	List<Object[]> findPipelinePerformanceBySubGeography(
			@Param("customerName") List<String> customerName,
			@Param("serviceLine") String serviceLine, @Param("iou") String iou,
			@Param("geography") String geography,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("salesStageFrom") int salesStageFrom,
			@Param("salesStageTo") int salesStageTo);

	@Query(value = "select GMT.geography, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '')"
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id and (CMT.customer_name in (:customerName) OR ('') in (:customerName))"
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code=9 and OPP.deal_closure_date between (:fromDate) and (:toDate) group by GMT.geography order by GMT.geography", nativeQuery = true)
	List<Object[]> findWinsPerformanceBySubGeography(
			@Param("customerName") List<String> customerName,
			@Param("serviceLine") String serviceLine, @Param("iou") String iou,
			@Param("geography") String geography,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	// list of top opportunities based on digital deal value
	@Query(value = "select distinct OPP.* from opportunity_t OPP"
			+ " JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id"
			+ " JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:subSp) OR (:subSp) = '')"
			+ " JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country"
			+ " JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography and (GMT.display_geography = (:geography) OR (:geography) = '')"
			+ " JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id and (CMT.customer_name in (:customerName) OR ('') in (:customerName)) "
			+ " JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '')"
			+ " where OPP.digital_deal_value <> 0 "
			+ "AND ((OPP.deal_closure_date between (:fromDate)  and (:toDate) and OPP.sales_stage_code >=9) or OPP.sales_stage_code < 9) "
			+ "AND (OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo))  order by OPP.digital_deal_value DESC limit (:count)", nativeQuery = true)
	public List<OpportunityT> getTopOpportunities(
			@Param("geography") String geography, @Param("subSp") String subSp,
			@Param("iou") String iou, @Param("fromDate") Date dateFrom,
			@Param("toDate") Date dateTo,
			@Param("salesStageFrom") int stageFrom,
			@Param("salesStageTo") int stageTo, @Param("count") int count,
			@Param("customerName") List<String> customerName);

	public List<OpportunityT> findBySalesStageCode(int salesStageCode);

	/**
	 * This method retrieves deal values of opportunities of users
	 * 
	 * @param users
	 * @return
	 */
	@Query(value = "select sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) "
			+ "/ (select conversion_rate from beacon_convertor_mapping_t where currency_name = 'USD')),OPP.sales_stage_code,count(*),SSM.sales_stage_description "
			+ "from opportunity_t OPP JOIN sales_stage_mapping_t SSM ON OPP.sales_stage_code=SSM.sales_stage_code "
			+ "where OPP.opportunity_id in ((select opportunity_id from opportunity_t where opportunity_owner in (:users)) "
			+ "union (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (:users)) "
			+ "union (select opportunity_id from bid_details_t BDT where BDT.bid_id in (select bid_id from bid_office_group_owner_link_t "
			+ "where bid_office_group_owner in (:users)))) AND ((OPP.sales_stage_code between 0 and 8) OR "
			+ "(OPP.deal_closure_date between (:fromDate) AND (:toDate))) "
			+ "group by OPP.sales_stage_code,SSM.sales_stage_description order by OPP.sales_stage_code", nativeQuery = true)
	public List<Object[]> findDealValueOfOpportunitiesBySupervisorId(
			@Param("users") List<String> users, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

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
	 * @param salesStageCode
	 * @return
	 */
	@Query(value = "select * from opportunity_t OPP WHERE OPP.opportunity_id in ((select opportunity_id from opportunity_t where opportunity_owner in (:users)) "
			+ "union (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (:users)) "
			+ "union (select opportunity_id from bid_details_t BDT where BDT.bid_id in "
			+ "(select bid_id from bid_office_group_owner_link_t where bid_office_group_owner in (:users)))) and (sales_stage_code = (:salesStageCode)) order by OPP.created_datetime desc", nativeQuery = true)
	List<OpportunityT> findTeamOpportunityDetailsBySupervisorId(
			@Param("users") List<String> users,
			@Param("salesStageCode") int salesStageCode);

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

	/**
	 * This query retrieves the opportunities for the users in the tables
	 * opportunity_t, opportunity_sales_support_link_t and bid_details_t
	 * 
	 * @param users
	 * @param startTs
	 * @param endTs
	 * @param salesStageCode
	 * @return
	 */
	@Query(value = "select * from opportunity_t OPP WHERE OPP.opportunity_id in ((select opportunity_id from opportunity_t where opportunity_owner in (:users)) "
			+ "union (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (:users)) "
			+ "union (select opportunity_id from bid_details_t BDT where BDT.bid_id in "
			+ "(select bid_id from bid_office_group_owner_link_t where bid_office_group_owner in (:users)))) and "
			+ "((OPP.deal_closure_date between (:startTs) and (:endTs) and OPP.sales_stage_code between 9 and 13) or (OPP.sales_stage_code < 9)) and (sales_stage_code = (:salesStageCode)) order by OPP.created_datetime desc", nativeQuery = true)
	List<OpportunityT> findTeamOpportunityDetailsBySupervisorIdInFinancialYear(
			@Param("users") List<String> users,
			@Param("startTs") Timestamp startTs,
			@Param("endTs") Timestamp endTs,
			@Param("salesStageCode") int salesStageCode);

	/**
	 * This query retrieves the opportunities for the users in the tables
	 * opportunity_t, opportunity_sales_support_link_t and bid_details_t
	 * 
	 * @param users
	 * @param startTs
	 * @param endTs
	 * @return
	 */
	@Query(value = "select * from opportunity_t OPP WHERE OPP.opportunity_id in ((select opportunity_id from opportunity_t where opportunity_owner in (:users)) "
			+ "union (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (:users)) "
			+ "union (select opportunity_id from bid_details_t BDT where BDT.bid_id in "
			+ "(select bid_id from bid_office_group_owner_link_t where bid_office_group_owner in (:users)))) and "
			+ "((OPP.deal_closure_date between (:startTs) and (:endTs) and OPP.sales_stage_code between 9 and 13) or (OPP.sales_stage_code < 9)) order by OPP.created_datetime desc", nativeQuery = true)
	List<OpportunityT> findTeamOpportunityDetailsBySupervisorIdInFinancialYear(
			@Param("users") List<String> users,
			@Param("startTs") Timestamp startTs, @Param("endTs") Timestamp endTs);
	
	@Query(value = "select * from opportunity_t OPP " 
			+ "JOIN customer_master_t CMT on OPP.customer_id = CMT.customer_id "
			+ "where (OPP.customer_id in (:customerIdList) or ('') in (:customerIdList)) " 
			+ "and (OPP.sales_stage_code in (:salesStageCode) or (-1) in (:salesStageCode)) "
			+ "and (OPP.strategic_deal=(:strategicDeal) or (:strategicDeal)='' ) "
			+ "and (OPP.new_logo = (:newLogo) or (:newLogo) ='' ) " 
			+ "and (((OPP.digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:dealCurrency)) between (:minDigitalDealValue) and (:maxDigitalDealValue)) or (:defaultDealRange)='YES') " 
			+ "and (OPP.digital_flag=(:digitalFlag) or (:digitalFlag)='' ) " 
			+ "and (CMT.iou in (select iou from iou_customer_mapping_t where display_iou in (:displayIou) or ('') in (:displayIou))) "
			+ "and (OPP.country in (:country) or ('') in (:country)) " 
			+ "and ((OPP.deal_closure_date between (:fromDate) and (:toDate) and OPP.sales_stage_code >= 9)or (OPP.sales_stage_code < 9)) "
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_partner_link_t where partner_id in (:partnerId)) or ('') in (:partnerId)) "
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_competitor_link_t where competitor_name in (:competitorName)) or ('') in (:competitorName)) "
			+ "and (((OPP.opportunity_id in (select entity_id from search_keywords_t where UPPER(search_keywords) similar to (:searchKeywords))) or (UPPER(OPP.opportunity_name) similar to (:opportunityName))) or ((:opportunityName) = (:searchKeywords) and (:opportunityName) = '')) "
			+ "and (OPP.opportunity_id in (select opportunity_id from bid_details_t where bid_request_type in (:bidRequestType)) or ('') in (:bidRequestType)) "
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_offering_link_t where offering in (:offering)) or ('') in (:offering)) " 
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_sub_sp_link_t where sub_sp in (select sub_sp from sub_sp_mapping_t where display_sub_sp in (:displaySubSp))) or ('') in (:displaySubSp)) " 
			+ "and ((OPP.opportunity_owner in (:userId) and (:isPrimaryOwner)) " 
		    + "or  (OPP.opportunity_id in (select opportunity_id from opportunity_sales_support_link_t where (sales_support_owner in (:userId) and (:isSalesSupportOwner)))) " 
			+ "or  (OPP.opportunity_id in (select BDT.opportunity_id from bid_details_t BDT where BDT.bid_id in (select bid_id from bid_office_group_owner_link_t where (bid_office_group_owner in (:userId) and (:isBidOfficeOwner))))) " 
			+ "or  ('') in (:userId)) "
			+ "order by OPP.modified_datetime", nativeQuery = true)
	List<OpportunityT> findByOpportunitiesForCurrentFyIgnoreCaseLike(
			@Param("customerIdList") List<String> customerIdList,
			@Param("salesStageCode") List<Integer> salesStageCode,
			@Param("strategicDeal") String strategicDeal,
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
			@Param("userId") List<String> userId,
			@Param("isPrimaryOwner") boolean isPrimaryOwner,
			@Param("isSalesSupportOwner") boolean isSalesSupportOwner,
			@Param("isBidOfficeOwner") boolean isBidOfficeOwner,
			@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate
			);
	
	
	@Query(value = "select * from opportunity_t OPP " 
			+ "JOIN customer_master_t CMT on OPP.customer_id = CMT.customer_id "
			+ "where (OPP.customer_id in (:customerIdList) or ('') in (:customerIdList)) " 
			+ "and (OPP.sales_stage_code in (:salesStageCode) or (-1) in (:salesStageCode)) "
			+ "and (OPP.strategic_deal=(:strategicDeal) or (:strategicDeal)='' ) "
			+ "and (OPP.new_logo = (:newLogo) or (:newLogo) ='' ) " 
			+ "and (((OPP.digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:dealCurrency)) between (:minDigitalDealValue) and (:maxDigitalDealValue)) or (:defaultDealRange)='YES') " 
			+ "and (OPP.digital_flag=(:digitalFlag) or (:digitalFlag)='' ) " 
			+ "and (CMT.iou in (select iou from iou_customer_mapping_t where display_iou in (:displayIou) or ('') in (:displayIou))) "
			+ "and (OPP.country in (:country) or ('') in (:country)) " 
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_partner_link_t where partner_id in (:partnerId)) or ('') in (:partnerId)) "
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_competitor_link_t where competitor_name in (:competitorName)) or ('') in (:competitorName)) "
			+ "and (((OPP.opportunity_id in (select entity_id from search_keywords_t where UPPER(search_keywords) similar to (:searchKeywords))) or (UPPER(OPP.opportunity_name) similar to (:opportunityName))) or ((:opportunityName) = (:searchKeywords) and (:opportunityName) = '')) "
			+ "and (OPP.opportunity_id in (select opportunity_id from bid_details_t where bid_request_type in (:bidRequestType)) or ('') in (:bidRequestType)) "
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_offering_link_t where offering in (:offering)) or ('') in (:offering)) " 
			+ "and (OPP.opportunity_id in (select opportunity_id from opportunity_sub_sp_link_t where sub_sp in (select sub_sp from sub_sp_mapping_t where display_sub_sp in (:displaySubSp))) or ('') in (:displaySubSp)) " 
			+ "and ((OPP.opportunity_owner in (:userId) and (:isPrimaryOwner)) " 
		    + "or (OPP.opportunity_id in (select opportunity_id from opportunity_sales_support_link_t where (sales_support_owner in (:userId) and (:isSalesSupportOwner)))) " 
			+ "or (OPP.opportunity_id in (select BDT.opportunity_id from bid_details_t BDT where BDT.bid_id in (select bid_id from bid_office_group_owner_link_t where (bid_office_group_owner in (:userId) and (:isBidOfficeOwner))))) " 
			+ "or ('') in (:userId)) "
			+ "order by OPP.modified_datetime", nativeQuery = true)
	List<OpportunityT> findByOpportunitiesIgnoreCaseLike(
			@Param("customerIdList") List<String> customerIdList,
			@Param("salesStageCode") List<Integer> salesStageCode,
			@Param("strategicDeal") String strategicDeal,
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
			@Param("userId") List<String> userId,
			@Param("isPrimaryOwner") boolean isPrimaryOwner,
			@Param("isSalesSupportOwner") boolean isSalesSupportOwner,
			@Param("isBidOfficeOwner") boolean isBidOfficeOwner);


	/**
	 * This query returns the sum of digital deal values for the given list of
	 * opportunity Ids
	 * 
	 * @param opportunityId
	 * @return Sum of digital deal values - Integer
	 */
	@Query(value = "select sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = 'USD')) from opportunity_t OPP where OPP.opportunity_id in (:opportunityId)", nativeQuery = true)
	Double findDigitalDealValueByOpportunityIdIn(
			@Param("opportunityId") List<String> opportunityId);

	// Detailed

	@Query(value = "select * from opportunity_t where opportunity_id = (:opportunityId)", nativeQuery = true)
	OpportunityT findOpportunityById(
			@Param("opportunityId") String opportunityId);
	

	@Query(value = "select distinct OPP.opportunity_id from opportunity_t OPP"
			+ " join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
			+ " join geography_mapping_t GMT on GMT.geography = GCMT.geography"
			+ " left outer join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id = OPP.opportunity_id"
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
			+ " left outer join bid_details_t BDT on OPP.opportunity_id = BDT.opportunity_id"
			+ " left outer join bid_office_group_owner_link_t BOG on BDT.bid_id = BOG.bid_id"
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
			+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " where  ((OPP.sales_stage_code in (:salesStage)) AND ((OPP.sales_stage_code between 0 and 8) OR"
			+ " (OPP.deal_closure_date between (:fromDate) AND (:toDate))))"
			+ " AND (OPP.country IN (:countryList) OR ('') in (:countryList)) "
			+ " AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))"
			+ " AND  (GMT.geography IN (:geoList) OR ('') in (:geoList))"
			+ " AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList)) "
			+ " AND ((OPP.opportunity_owner IN (:userIds) OR OSSLT.sales_support_owner IN (:userIds) OR "
			+ "BOG.bid_office_group_owner in (:userIds)) OR ('') in (:userIds))", nativeQuery = true)
	List<String> findOpportunitiesByRoleWith(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate,
			@Param("salesStage") List<Integer> salesStage,
			@Param("userIds") List<String> userIds,
			@Param("geoList") List<String> geoList,
			@Param("countryList") List<String> countryList,
			@Param("iouList") List<String> iouList,
			@Param("serviceLines") List<String> serviceLines);

	@Query(value = "select distinct OPP.opportunity_id from opportunity_t OPP"
			+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
			+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
			+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " where (GMT.geography IN (:geography) OR ('') in (:geography)) AND"
			+ " (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))"
			+ " AND ((OPP.sales_stage_code in (:salesStage)) AND ((OPP.sales_stage_code between 0 and 8) OR  (OPP.deal_closure_date between (:fromDate) AND (:toDate))))"
			+ " AND (OPP.country IN (:country) OR ('') in (:country)) "
			+ " AND (ICM.display_iou IN (:iou) OR ('') in (:iou)) ", nativeQuery = true)
	List<String> findOpportunitiesWith(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate,
			@Param("geography") List<String> geography,
			@Param("country") List<String> country,
			@Param("iou") List<String> iou,
			@Param("serviceLines") List<String> serviceLines,
			@Param("salesStage") List<Integer> salesStage);

	// Detailed Ends Here

	// Win or Loss query

	// +
	// " inner join bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id"
	// +
	// " inner join sales_stage_mapping_t SASMT on opp.sales_stage_code = SASMT.sales_stage_code "

	@Query(value = "select distinct displaySubSp, case when oppCount is not null then oppCount else 0 end as noOfBids, sum(digitalDealValue) as dealValue "
			+ " from (select distinct SSMT.display_sub_sp as displaySubSp, count(oppt.opportunity_id) as oppCount, case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t "
			+ " where currency_name= OPPT.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t "
			+ " where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPPT.deal_currency)) /"
			+ " (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR')))	else 0 end as digitalDealValue from opportunity_t OPPT "
			+ " left join opportunity_sub_sp_link_t ssl on OPPT.opportunity_id = ssl.opportunity_id	left join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp) where oppt.opportunity_id in ("
			+ " select distinct OPP.opportunity_id from opportunity_t OPP join geography_country_mapping_t GCMT on (GCMT.country=OPP.country AND (OPP.country IN (:countryList) OR ('') in (:countryList)))"
			+ " 	join geography_mapping_t GMT on (GMT.geography = GCMT.geography AND (GCMT.geography IN (:geoList) OR ('') in (:geoList)))"
			+ " 	left join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ "	left join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)))"
			+ "	inner join customer_master_t CMT on opp.customer_id = CMT.customer_id "
			+ " inner join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList))) "
			+ "	left join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id = OPP.opportunity_id "
			+ " left join bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id"
			+ " left join bid_office_group_owner_link_t BOG on BDT.bid_id = BOG.bid_id "
			+ "where subsp_primary=true and  opp.sales_stage_code IN (:salesStageCode)"
			+ "	AND ((OPP.sales_stage_code between 0 and 8) OR (opp.deal_closure_date between (:fromDate) AND (:toDate))) "
			+ " AND (OPP.opportunity_owner IN (:userIds) OR OSSLT.sales_support_owner IN (:userIds) OR"
			+ " BOG.bid_office_group_owner in (:userIds) OR"
			+ " ('') in (:userIds))) group by displaySubSp) as oppPipelineBySubSp group by displaySubSp, oppCount order by displaySubSp", nativeQuery = true)
	List<Object[]> findOpportunitiesWithServiceLineByRole(
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("salesStageCode") int salesStageCode,
			@Param("userIds") List<String> userIds,
			@Param("geoList") List<String> geoList,
			@Param("countryList") List<String> countryList,
			@Param("iouList") List<String> iouList,
			@Param("serviceLines") List<String> serviceLines);

	@Query(value = "select distinct SSMT.display_sub_sp,count(SSMT.display_sub_sp),case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digital_deal_value from opportunity_t OPP"
			+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
			+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
			+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " where subsp_primary=true and (GMT.geography IN (:geography) OR ('') in (:geography))"
			+ " AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))"
			+ " AND (opp.sales_stage_code IN (:salesStageCode)) "
			+ " AND ((OPP.sales_stage_code between 0 and 8) OR (opp.deal_closure_date  between (:fromDate) AND (:toDate)))"
			+ " AND (OPP.country IN (:country) OR ('') in (:country)) AND (ICM.display_iou IN (:iou) OR ('') in (:iou))"
			+ " group by SSMT.display_sub_sp", nativeQuery = true)
	List<Object[]> findOpportunitiesWithServiceLine(
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("geography") List<String> geography,
			@Param("country") List<String> country,
			@Param("iou") List<String> iou,
			@Param("serviceLines") List<String> serviceLines,
			@Param("salesStageCode") int salesStageCode);

	@Query(value =  "select distinct displayGeo, case when oppCount is not null then oppCount else 0 end as noOfBids, sum(digitalDealValue) "
			+ " as dealValue from (select distinct GMT.display_geography as displayGeo, count(oppt.opportunity_id) as oppCount, "
			+ " case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t "
			+ " where currency_name= OPPT.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then "
			+ " sum((digital_deal_value * ( select conversion_rate from beacon_convertor_mapping_t where currency_name=OPPT.deal_currency)) /"
			+ " (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digitalDealValue from opportunity_t OPPT "
			+ " join geography_country_mapping_t GCMT on (GCMT.country=OPPT.country) join geography_mapping_t GMT on (GMT.geography = GCMT.geography) "
			+ " where oppt.opportunity_id in (select distinct OPP.opportunity_id from opportunity_t OPP join geography_country_mapping_t GCMT on (GCMT.country=OPP.country AND (OPP.country IN (:countryList) OR ('') in (:countryList))) "
			+ " join geography_mapping_t GMT on (GMT.geography = GCMT.geography AND (GCMT.geography IN (:geoList) OR ('') in (:geoList))) "
			+ " left join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)))  "
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id inner join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList))) "
			+ " left join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id = OPP.opportunity_id  "
			+ " left join bid_details_t BDT on OPP.opportunity_id = BDT.opportunity_id"
			+ " left join bid_office_group_owner_link_t BOG on BDT.bid_id = BOG.bid_id"
			+ " where OPP.sales_stage_code in (:salesStage) and ((OPP.sales_stage_code between 0 and 8)"
			+ " OR (opp.deal_closure_date between (:fromDate) AND (:toDate))) "
			+ " AND (OPP.opportunity_owner IN (:userIds) OR OSSLT.sales_support_owner IN (:userIds) OR "
			+ " BOG.bid_office_group_owner in (:userIds) OR "
			+ "('') in (:userIds))) "
			+ " group by displayGeo) as oppPipelineByGeo group by displayGeo, oppCount order by displayGeo", nativeQuery = true)
	List<Object[]> findOpportunitiesWithGeographyByRole(
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("salesStage") int salesStage,
			@Param("userIds") List<String> userIds,
			@Param("geoList") List<String> geoList,
			@Param("countryList") List<String> countryList,
			@Param("iouList") List<String> iouList,
			@Param("serviceLines") List<String> serviceLines);

	@Query(value = "select distinct GMT.display_geography,count(GMT.display_geography),case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digital_deal_value from opportunity_t OPP"
			+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
			+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
			+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " where (GMT.geography IN (:geography) OR ('') in (:geography))"
			+ " AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))"
			+ " AND ((opp.sales_stage_code IN (:salesStage)) "
			+ " AND ((OPP.sales_stage_code between 0 and 8) OR (opp.deal_closure_date  between (:fromDate) AND (:toDate))))"
			+ " AND (OPP.country IN (:country) OR ('') in (:country)) AND (ICM.display_iou IN (:iou) OR ('') in (:iou))"
			+ " group by GMT.display_geography", nativeQuery = true)
	List<Object[]> findOpportunitiesWithGeography(
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("geography") List<String> geography,
			@Param("country") List<String> country,
			@Param("iou") List<String> iou,
			@Param("serviceLines") List<String> serviceLines,
			@Param("salesStage") int salesStage);

	@Query(value = "select distinct displayIou, case when oppCount is not null then oppCount else 0 end as noOfBids, sum(digitalDealValue) "
			+ " as dealValue from ( select distinct ICM.display_iou as displayIou, count(oppt.opportunity_id) as oppCount,  "
			+ " case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t  "
			+ " where currency_name= OPPT.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then "
			+ " sum((digital_deal_value * ( select conversion_rate from beacon_convertor_mapping_t where currency_name=OPPT.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digitalDealValue from opportunity_t OPPT "
			+ " inner join customer_master_t CMT on oppt.customer_id = CMT.customer_id  inner join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou) where oppt.opportunity_id in "
			+ " (select distinct OPP.opportunity_id from opportunity_t OPP "
			+ " join geography_country_mapping_t GCMT on (GCMT.country=OPP.country AND (OPP.country IN (:countryList) OR ('') in (:countryList))) join geography_mapping_t GMT on (GMT.geography = GCMT.geography AND (GCMT.geography IN (:geoList) OR ('') in (:geoList)))"
			+ " left join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id  left join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))) "
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id  inner join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList))) "
			+ " left join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id = OPP.opportunity_id "
			+ " left join bid_details_t BDT on OPP.opportunity_id = BDT.opportunity_id"
			+ " left join bid_office_group_owner_link_t BOG on BDT.bid_id = BOG.bid_id"
			+ " where opp.sales_stage_code IN (:salesStage) AND (opp.sales_stage_code between 0 and 8 OR (opp.deal_closure_date  between (:fromDate) AND (:toDate))) "
			+ " AND (OPP.opportunity_owner IN (:userIds) OR OSSLT.sales_support_owner IN (:userIds) OR BOG.bid_office_group_owner in (:userIds) OR ('') in (:userIds))) "
			+ " group by displayIou) as oppPipelineByIou group by displayIou, oppCount order by displayIou", nativeQuery = true)
	List<Object[]> findOpportunitiesWithIouByRole(
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("salesStage") int salesStage,
			@Param("userIds") List<String> userIds,
			@Param("geoList") List<String> geoList,
			@Param("countryList") List<String> countryList,
			@Param("iouList") List<String> iouList,
			@Param("serviceLines") List<String> serviceLines);

	@Query(value = "select distinct ICM.display_iou,count(ICM.display_iou),case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digital_deal_value from opportunity_t OPP"
			+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
			+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
			+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " where (GMT.geography IN (:geography) OR ('') in (:geography))"
			+ " AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))"
			+ " AND (opp.sales_stage_code IN (:salesStage)) "
			+ " AND ((OPP.sales_stage_code between 0 and 8) OR (opp.deal_closure_date  between (:fromDate) AND (:toDate)))"
			+ " AND (OPP.country IN (:country) OR ('') in (:country)) AND (ICM.display_iou IN (:iou) OR ('') in (:iou))"
			+ " group by ICM.display_iou", nativeQuery = true)
	List<Object[]> findOpportunitiesWithIou(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate,
			@Param("geography") List<String> geography,
			@Param("country") List<String> country,
			@Param("iou") List<String> iou,
			@Param("serviceLines") List<String> serviceLines,
			@Param("salesStage") int salesStage);

	// Win or Loss ends here

	// Anticipating or Pipeline Query

	@Query(value = "select sales_stage_description, case when oppCount is not null then oppCount else 0 end as noOfBids, display_geography, sum(digitalDealValue) as dealValue from ( "
			+ " select distinct sales_stage_description, count(opportunity_id) as oppCount, display_geography, case when sum((digital_deal_value * ( "
			+ " select conversion_rate from beacon_convertor_mapping_t where currency_name= OPPT.deal_currency)) / (select conversion_rate "
			+ " from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * ("
			+ " select conversion_rate from beacon_convertor_mapping_t where currency_name=OPPT.deal_currency)) / ("
			+ " select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digitalDealValue from opportunity_t OPPT"
			+ " join geography_country_mapping_t GCMT on (GCMT.country=OPPT.country) join geography_mapping_t GMT on (GMT.geography = GCMT.geography)"
			+ " inner join sales_stage_mapping_t SASMT on OPPT.sales_stage_code = SASMT.sales_stage_code"
			+ " where opportunity_id in (select distinct OPP.opportunity_id from opportunity_t OPP join geography_country_mapping_t GCMT on (GCMT.country=OPP.country AND (OPP.country IN (:countryList) OR ('') in (:countryList))) "
			+ " join geography_mapping_t GMT on (GMT.geography = GCMT.geography AND (GCMT.geography IN (:geoList) OR ('') in (:geoList))) "
			+ " left join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))) inner join customer_master_t CMT on opp.customer_id = CMT.customer_id "
			+ " inner join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList))) left join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id = OPP.opportunity_id "
			+ " left join bid_details_t BDT on OPP.opportunity_id = BDT.opportunity_id"
			+ " left join bid_office_group_owner_link_t BOG on BDT.bid_id = BOG.bid_id"
			+ " where (opp.sales_stage_code IN (:salesStage)) AND ((OPP.opportunity_owner IN (:userIds) OR OSSLT.sales_support_owner IN (:userIds) OR BOG.bid_office_group_owner in (:userIds)) OR ('') in (:userIds))"
			+ " group by  OPP.opportunity_id) group by sales_stage_description, display_geography) as oppPipelineByGeo group by sales_stage_description,display_geography,oppCount order by sales_stage_description", nativeQuery = true)
	List<Object[]> findSummaryGeographyByRole(
			@Param("salesStage") int salesStage,
			@Param("userIds") List<String> userIds,
			@Param("geoList") List<String> geoList,
			@Param("countryList") List<String> countryList,
			@Param("iouList") List<String> iouList,
			@Param("serviceLines") List<String> serviceLines);

	@Query(value = "select distinct SASMT.sales_stage_description,case when count(opp.opportunity_id) is not null then count(opp.opportunity_id) else 0 end as noOfBids,GMT.display_geography,case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as bidValue"
			+ " from opportunity_t OPP"
			+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
			+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
			+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " inner join sales_stage_mapping_t SASMT on opp.sales_stage_code = SASMT.sales_stage_code"
			+ " where (GMT.geography IN (:geography) OR ('') in (:geography)) "
			+ " AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)) "
			+ " AND (opp.sales_stage_code IN (:salesStage)) "
			+ " AND (OPP.country IN (:country) OR ('') in (:country)) "
			+ " AND (ICM.display_iou IN (:iou) OR ('') in (:iou)) "
			+ " group by GMT.display_geography,SASMT.sales_stage_code", nativeQuery = true)
	List<Object[]> findSummaryGeography(
			@Param("geography") List<String> geography,
			@Param("country") List<String> country,
			@Param("iou") List<String> iou,
			@Param("serviceLines") List<String> serviceLines,
			@Param("salesStage") int salesStage);

	@Query(value = "select COALESCE(displaySubSp, 'SubSp Not Defined'), case when oppCount is not null then oppCount else 0 end as noOfBids, sum(digitalDealValue) as dealValue from ( "
			+ "select distinct SSMT.display_sub_sp as displaySubSp, count(oppt.opportunity_id) as oppCount, case when sum((digital_deal_value * ("
			+ "select conversion_rate from beacon_convertor_mapping_t where currency_name= OPPT.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * ("
			+ " select conversion_rate from beacon_convertor_mapping_t where currency_name=OPPT.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digitalDealValue "
			+ " from opportunity_t OPPT left join opportunity_sub_sp_link_t ssl on oppt.opportunity_id = ssl.opportunity_id left join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp) "
			+ " inner join sales_stage_mapping_t SASMT on OPPT.sales_stage_code = SASMT.sales_stage_code where oppt.opportunity_id in (select distinct OPP.opportunity_id from opportunity_t OPP join geography_country_mapping_t GCMT on (GCMT.country=OPP.country AND (OPP.country IN (:countryList) OR ('') in (:countryList)))"
			+ " join geography_mapping_t GMT on (GMT.geography = GCMT.geography AND (GCMT.geography IN (:geoList) OR ('') in (:geoList))) "
			+ " left join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))) "
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id "
			+ " inner join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList))) "
			+ " left join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id = OPP.opportunity_id "
			+ " left join bid_details_t BDT on OPP.opportunity_id = BDT.opportunity_id"
			+ " left join bid_office_group_owner_link_t BOG on BDT.bid_id = BOG.bid_id"
			+ " where subsp_primary=true and (opp.sales_stage_code IN (:salesStage)) AND ((OPP.opportunity_owner IN (:userIds) OR OSSLT.sales_support_owner IN (:userIds) OR BOG.bid_office_group_owner in (:userIds)) OR ('') in (:userIds)) "
			+ " group by  OPP.opportunity_id) group by displaySubSp) as oppPipelineBySubSp group by displaySubSp, oppCount order by displaySubSp", nativeQuery = true)
	List<Object[]> findPipelineSummaryServiceLineByRole(
			@Param("salesStage") List<Integer> salesStage,
			@Param("userIds") List<String> userIds,
			@Param("geoList") List<String> geoList,
			@Param("countryList") List<String> countryList,
			@Param("iouList") List<String> iouList,
			@Param("serviceLines") List<String> serviceLines);

	@Query(value = "select COALESCE(SSMT.display_sub_sp, 'SubSp Not Defined'),case when count(opp.opportunity_id) is not null then count(opp.opportunity_id) else 0 end as noOfBids,case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as bidValue"
			+ " from opportunity_t OPP"
			+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
			+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
			+ " left join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
			+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " inner join sales_stage_mapping_t SASMT on opp.sales_stage_code = SASMT.sales_stage_code"
			+ " where subsp_primary=true and (GMT.geography IN (:geography) OR ('') in (:geography))"
			+ " AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))"
			+ " AND (opp.sales_stage_code IN (:salesStage))"
			+ " AND (OPP.country IN (:country) OR ('') in (:country))"
			+ " AND (ICM.display_iou IN (:iou) OR ('') in (:iou))"
			+ " group by SSMT.display_sub_sp", nativeQuery = true)
	List<Object[]> findPipelineSummaryServiceLine(
			@Param("geography") List<String> geography,
			@Param("country") List<String> country,
			@Param("iou") List<String> iou,
			@Param("serviceLines") List<String> serviceLines,
			@Param("salesStage") List<Integer> salesStage);

	@Query(value = "select sales_stage_description, case when oppCount is not null then oppCount else 0 end as noOfBids, display_iou, sum(digitalDealValue) as dealValue from ( "
			+ " select distinct sales_stage_description, count(opportunity_id) as oppCount, display_iou, case when sum((digital_deal_value * ("
			+ " select conversion_rate from beacon_convertor_mapping_t where currency_name= OPPT.deal_currency)) / (select conversion_rate"
			+ " from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * ("
			+ "select conversion_rate from beacon_convertor_mapping_t where currency_name=OPPT.deal_currency)) / ("
			+ "select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digitalDealValue from opportunity_t OPPT "
			+ " inner join customer_master_t CMT on oppt.customer_id = CMT.customer_id inner join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou) "
			+ " inner join sales_stage_mapping_t SASMT on OPPT.sales_stage_code = SASMT.sales_stage_code "
			+ " where opportunity_id in (select distinct OPP.opportunity_id from opportunity_t OPP join geography_country_mapping_t GCMT on (GCMT.country=OPP.country AND (OPP.country IN (:countryList) OR ('') in (:countryList)))"
			+ " join geography_mapping_t GMT on (GMT.geography = GCMT.geography AND (GCMT.geography IN (:geoList) OR ('') in (:geoList))) "
			+ " left join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left join bid_details_t BDT on OPP.opportunity_id = BDT.opportunity_id"
			+ " left join bid_office_group_owner_link_t BOG on BDT.bid_id = BOG.bid_id"
			+ " left join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))) "
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id  inner join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList)))"
			+ " left join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id = OPP.opportunity_id where (opp.sales_stage_code IN (:salesStage)) AND ((OPP.opportunity_owner IN (:userIds)"
			+ " OR OSSLT.sales_support_owner IN (:userIds) OR BOG.bid_office_group_owner in (:userIds)) OR ('') in (:userIds)) group by OPP.opportunity_id) group by sales_stage_description, display_iou) as oppPipelineByIou group by sales_stage_description,display_iou,oppCount ", nativeQuery = true)
	List<Object[]> findSummaryIouByRole(@Param("salesStage") int salesStage,
			@Param("userIds") List<String> userIds,
			@Param("geoList") List<String> geoList,
			@Param("countryList") List<String> countryList,
			@Param("iouList") List<String> iouList,
			@Param("serviceLines") List<String> serviceLines);

	@Query(value = "select distinct SASMT.sales_stage_description,case when count(opp.opportunity_id) is not null then count(opp.opportunity_id) else 0 end as noOfBids,ICM.display_iou,case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as bidValue"
			+ " from opportunity_t OPP"
			+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
			+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
			+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " inner join sales_stage_mapping_t SASMT on opp.sales_stage_code = SASMT.sales_stage_code"
			+ " where (GMT.geography IN (:geography) OR ('') in (:geography)) "
			+ " AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)) "
			+ " AND (opp.sales_stage_code IN (:salesStage)) "
			+ " AND (OPP.country IN (:country) OR ('') in (:country)) "
			+ " AND (ICM.display_iou IN (:iou) OR ('') in (:iou)) "
			+ " group by ICM.display_iou,SASMT.sales_stage_code", nativeQuery = true)
	List<Object[]> findSummaryIou(@Param("geography") List<String> geography,
			@Param("country") List<String> country,
			@Param("iou") List<String> iou,
			@Param("serviceLines") List<String> serviceLines,
			@Param("salesStage") int salesStage);

	// Anticipating or pipeline ends here

	// @Query(value =
	// "select * from opportunity_t where sales_stage_code in (9,10) and deal_closure_date is not null order by deal_closure_date",
	// nativeQuery = true)
	// List<OpportunityT> getAllYear();

	// @Query(value =
	// "select * from opportunity_t where sales_stage_code in (9,10,11,12,13) and deal_closure_date is not null order by deal_closure_date",
	// nativeQuery = true)
	// List<OpportunityT> getAllYearInDetailed();


	@Query(value = " select result, opportunity_id as opportunity, is_name , created_datetime from ( "
			+ " select opportunity_name as result , opportunity_id , 't' as is_name, created_datetime from opportunity_t  where UPPER(opportunity_name) like ?1 "
			+ " union "
			+ " select SKT.search_keywords as result, SKT.entity_id as opportunity_id ,'f' as is_name , OPP.created_datetime as created_datetime from search_keywords_t SKT JOIN opportunity_t OPP on OPP.opportunity_id=SKT.entity_id where SKT.entity_type ='OPPORTUNITY' and UPPER(search_keywords) like ?2 "
			+ " ) as search order by created_datetime desc", nativeQuery = true)
	ArrayList<Object[]> findOpportunityNameOrKeywords(String name,
			String keyword);

	@Query(value = "select OPP.country, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "and (GCMT.geography =(:geography) OR (:geography) = '')"
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id and (CMT.customer_name in (:customerName) OR ('') in (:customerName))"
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 and ((OPP.sales_stage_code >= 9 and deal_closure_date between (:fromDate) and (:toDate)) or OPP.sales_stage_code < 9) "
			+ "and OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo)"
			+ "group by OPP.country order by OPP.country", nativeQuery = true)
	List<Object[]> findPipelinePerformanceByCountry(
			@Param("customerName") List<String> customerName,
			@Param("serviceLine") String serviceLine, @Param("iou") String iou,
			@Param("geography") String geography,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("salesStageFrom") int salesStageFrom,
			@Param("salesStageTo") int salesStageTo);

	@Query(value = "select OPP.country, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on (GCMT.geography =(:geography) OR (:geography) = '')"
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id and (CMT.customer_name in (:customerName) OR ('') in (:customerName))"
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.digital_deal_value <> 0 and OPP.sales_stage_code=9 and OPP.deal_closure_date between (:fromDate) and (:toDate) group by OPP.country order by OPP.country", nativeQuery = true)
	List<Object[]> findWinsPerformanceByCountry(
			@Param("customerName") List<String> customerName,
			@Param("serviceLine") String serviceLine, @Param("iou") String iou,
			@Param("geography") String geography,
			@Param("currency") String currency,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select distinct(user_id) from ( "
			+ "select opportunity_owner as user_id from opportunity_t where opportunity_id =(:opportunityId) union "
			+ "select sales_support_owner as user_id from opportunity_sales_support_link_t	where opportunity_id =(:opportunityId) union "
			+ "select bid_office_group_owner as user_id from bid_office_group_owner_link_t  where bid_id  in (select bid_id from bid_details_t where opportunity_id =(:opportunityId))) as opportunity_owners", nativeQuery = true)
	List<String> getAllOwners(@Param("opportunityId") String opportunityId);

	@Query(value = "select OPP.deal_closure_date, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currencyName)))  as digitalDealCalue from opportunity_t OPP"
			+ "	join geography_country_mapping_t GCMT on GCMT.country=OPP.country and (GCMT.geography=(:geography) or (:geography) = '') "
			+ " join geography_mapping_t GMT on GMT.geography=GCMT.geography and (GMT.display_geography=(:displayGeography) or (:displayGeography)='') "
			+ " join opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id=OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ "JOIN customer_master_t CMT on OPP.customer_id = CMT.customer_id and (CMT.customer_name in (:custName) or ('') in (:custName) ) "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "where OPP.deal_closure_date between (:fromDate) and (:toDate) and OPP.sales_stage_code=9 "
			+ "group by OPP.deal_closure_date", nativeQuery = true)
	List<Object[]> getDigitalDealValueByClosureDate(
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("displayGeography") String displayGeography,
			@Param("geography") String geography,
			@Param("serviceLine") String serviceLine, @Param("iou") String iou,
			@Param("custName") List<String> custName,
			@Param("currencyName") String currencyName);

	@Query(value = "SELECT SUM(PRIMARY_BID_VALUE) as oppOwnerDealValue,SUM(SALES_VALUE) as salesOwnerDealValue FROM ( "
			+ " select opp.opportunity_id, opp.deal_currency, sum((digital_deal_value * "
			+ " (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) AS PRIMARY_BID_VALUE, (0.0) AS SALES_VALUE from opportunity_t OPP "
			+ " left outer join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id left outer join bid_office_group_owner_link_t bofg on bidt.bid_id = bofg.bid_id where sales_stage_code = 9 and (OPP.opportunity_owner = (:userId) or (bofg.bid_office_group_owner = (:userId))) "
			+ " and deal_closure_date between (:fromDate) and (:toDate) group by opp.opportunity_id, opp.deal_currency "
			+ " UNION select OPP.opportunity_id ,opp.deal_currency, (0.0) AS PRIMARY_BID_VALUE ,"
			+ "sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) AS SALES_VALUE "
			+ "from opportunity_t OPP join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id where sales_stage_code = 9 "
			+ "and OSSLT.sales_support_owner = (:userId) and deal_closure_date between (:fromDate) and (:toDate) "
			+ "group by OPP.opportunity_id, opp.deal_currency ) AS OppWinValue ", nativeQuery = true)
	Object[][] findOpportunityWinValueByOpportunityOwnerOrSalesSupportOwner(
			@Param("userId") String userId, @Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate);

	@Query(value = " select SUM(noOfPrimaryBids) as primarySum, SUM(noOfSecondaryBids) as secondarySum from ( "
			+ " select OPPTLH.opportunity_id, count(distinct(sales_stage_code)) as noOfPrimaryBids, (0) as noOfSecondaryBids "
			+ " from Opportunity_timeline_history_t  OPPTLH where sales_stage_code in (2,4) and opportunity_id in ( "
			+ " select distinct OPP.opportunity_id from opportunity_t OPP where OPP.opportunity_owner = (:userId) and (sales_stage_code>8 and deal_closure_date "
			+ " between (:fromDate) and (:toDate)) UNION select distinct OPP.opportunity_id from opportunity_t OPP "
			+ " join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id "
			+ " join bid_office_group_owner_link_t bofg on bidt.bid_id = bofg.bid_id "
			+ " where bofg.bid_office_group_owner = (:userId) and (sales_stage_code>8 and deal_closure_date "
			+ " between (:fromDate) and (:toDate))) group by OPPTLH.opportunity_id UNION select opportunity_id, (0) as noOfPrimaryBids, "
			+ " count(distinct(sales_stage_code)) as noOfSecondaryBids from Opportunity_timeline_history_t "
			+ " where sales_stage_code in (2,4) and opportunity_id in (select (OSSLT.opportunity_id) AS SECONDARY "
			+ " from opportunity_t OPP join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id "
			+ " where OSSLT.sales_support_owner = (:userId) and (sales_stage_code>8 and deal_closure_date between (:fromDate) and (:toDate))) "
			+ " group by opportunity_id) as ProposalsSupported ", nativeQuery = true)
	Object[][] findProposalSupportedByOpportunityOwnerOrSalesSupportOwnerByPreviousQuarterOrYear(
			@Param("userId") String userId, @Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate);

	@Query(value =  "select SUM(noOfPrimaryBids) as primarySum, SUM(noOfSecondaryBids) as secondarySum from ( "
			+ " select OPPTLH.opportunity_id, count(distinct(sales_stage_code)) as noOfPrimaryBids, (0) as noOfSecondaryBids "
			+ " from Opportunity_timeline_history_t OPPTLH where sales_stage_code in (2,4) and opportunity_id in ( "
			+ " select distinct OPP.opportunity_id from opportunity_t OPP where (OPP.opportunity_owner = (:userId) and sales_stage_code>=0 and (sales_stage_code>8 and deal_closure_date " 
			+ " between (:fromDate) and (:toDate) or (sales_stage_code<9)))  "
			+ " UNION select distinct OPP.opportunity_id from opportunity_t OPP join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id "
			+ " join bid_office_group_owner_link_t bofg on (bidt.bid_id = bofg.bid_id and bofg.bid_office_group_owner = (:userId)) "
			+ " where sales_stage_code>=0  and (sales_stage_code>8 and deal_closure_date between (:fromDate) and (:toDate)) or (sales_stage_code<9)) "
			+ "  group by OPPTLH.opportunity_id UNION select opportunity_id, (0) as noOfPrimaryBids, "
			+ "  count(distinct(sales_stage_code)) as noOfSecondaryBids from Opportunity_timeline_history_t "
			+ "  where sales_stage_code in (2,4) and opportunity_id in (select distinct (OSSLT.opportunity_id) AS SECONDARY "
			+ " from opportunity_t OPP join opportunity_sales_support_link_t OSSLT on (OSSLT.opportunity_id=OPP.opportunity_id "
			+ " and OSSLT.sales_support_owner = (:userId)) where sales_stage_code>=0 and (sales_stage_code>8 and deal_closure_date between (:fromDate) and (:toDate)) or "
			+ " (sales_stage_code<9)) group by opportunity_id) as ProposalsSupported ", nativeQuery = true)
	Object[][] findProposalSupportedByOpportunityOwnerOrSalesSupportOwnerByCurrentQuarterOrYear(
			@Param("userId") String userId, @Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate);


	@Query(value = "select sum(oppOwnerDealValue) from (SELECT (opportunity_id),SUM(PRIMARY_BID_VALUE) as oppOwnerDealValue "
			+ " FROM (select opp.opportunity_id, opp.deal_currency, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) AS PRIMARY_BID_VALUE "
			+ " from opportunity_t OPP left outer join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id "
			+ "where sales_stage_code = '9' and (OPP.opportunity_owner in (:userIds)) and deal_closure_date between (:fromDate) and (:toDate) "
			+ " group by opportunity_owner ,opp.opportunity_id, opp.deal_currency "
			+ " UNION select opp.opportunity_id, opp.deal_currency, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) AS PRIMARY_BID_VALUE from opportunity_t OPP "
			+ " join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id join bid_office_group_owner_link_t bofg on bidt.bid_id = bofg.bid_id "
			+ " where sales_stage_code = '9' and (bofg.bid_office_group_owner in (:userIds)) and deal_closure_date between (:fromDate) and (:toDate) "
			+ " group by opportunity_owner ,opp.opportunity_id, opp.deal_currency "
			+ " UNION select OPP.opportunity_id ,opp.deal_currency, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t "
			+ " where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) AS SALES_VALUE from opportunity_t OPP "
			+ " join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id "
			+ " where sales_stage_code = '9' and OSSLT.sales_support_owner in (:userIds) and deal_closure_date between (:fromDate) and (:toDate) " 
			+ " group by sales_support_owner, OPP.opportunity_id, opp.deal_currency) AS OppWinValue GROUP BY opportunity_id "
			+ " ) as OppWins", nativeQuery = true)
	BigDecimal getTotalOpportunityWinsByUserIds(
			@Param("userIds") List<String> userIds,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select SUM(noOfPrimaryBids+noOfSecondaryBids) as totalProposalSupported  from (select OPPTLH.opportunity_id, count(distinct(sales_stage_code)) as noOfPrimaryBids, (0) as noOfSecondaryBids from Opportunity_timeline_history_t OPPTLH "
			+ " where sales_stage_code in (2,4) and opportunity_id in (select distinct OPP.opportunity_id from opportunity_t OPP "
			+ " where (OPP.opportunity_owner in (:userIds) and sales_stage_code>=0 and (sales_stage_code>8 and deal_closure_date between (:fromDate) and (:toDate) or (sales_stage_code<9)))"
			+ " UNION select distinct OPP.opportunity_id from opportunity_t OPP join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id "
			+ " join bid_office_group_owner_link_t bofg on (bidt.bid_id = bofg.bid_id and bofg.bid_office_group_owner in (:userIds)) "
			+ " where sales_stage_code>=0 and (sales_stage_code>8 and deal_closure_date between (:fromDate) and (:toDate)) or (sales_stage_code<9)) "
			+ " group by OPPTLH.opportunity_id "
			+ " UNION select opportunity_id, (0) as noOfPrimaryBids, count(distinct(sales_stage_code)) as noOfSecondaryBids from "
			+ " Opportunity_timeline_history_t where sales_stage_code in (2,4) and opportunity_id in (select distinct (OSSLT.opportunity_id) AS SECONDARY from opportunity_t OPP "
			+ " join opportunity_sales_support_link_t OSSLT on (OSSLT.opportunity_id=OPP.opportunity_id and OSSLT.sales_support_owner in (:userIds)) "
			+ " where sales_stage_code>=0 and (sales_stage_code>8 and deal_closure_date between (:fromDate) and (:toDate)) or (sales_stage_code<9)) "
			+ " group by opportunity_id) as ProposalsSupported", nativeQuery = true)
	BigInteger getTotalProposalSupportedByUserIds(
			@Param("userIds") List<String> userIds,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select case when sum(oppOwnerDealValue) is not null then sum(oppOwnerDealValue) else 0.0 end as deal_value_sum from (SELECT (opportunity_id),SUM(PRIMARY_BID_VALUE) as oppOwnerDealValue FROM ( "
			+ " select opp.opportunity_id, opp.deal_currency, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) "
			+ " AS PRIMARY_BID_VALUE from opportunity_t OPP join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id "
			+ " where sales_stage_code in (4,5,6,7,8)  and (OPP.opportunity_owner = (:userId)) and deal_closure_date "
			+ " between (:fromDate) and (:toDate)  group by opportunity_owner ,opp.opportunity_id, opp.deal_currency "
			+ " UNION select opp.opportunity_id, opp.deal_currency, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) "
			+ " AS PRIMARY_BID_VALUE from opportunity_t OPP join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id "
			+ " join bid_office_group_owner_link_t bofg on bidt.bid_id = bofg.bid_id where sales_stage_code in (4,5,6,7,8) and (bofg.bid_office_group_owner = (:userId)) and deal_closure_date "
			+ " between (:fromDate) and (:toDate)  group by opportunity_owner ,opp.opportunity_id, opp.deal_currency "
			+ " UNION select OPP.opportunity_id ,opp.deal_currency, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) "
			+ " AS SALES_VALUE from opportunity_t OPP join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id "
			+ " where sales_stage_code in (4,5,6,7,8) and OSSLT.sales_support_owner = (:userId) and deal_closure_date between  (:fromDate) and (:toDate) "
			+ " group by sales_support_owner, OPP.opportunity_id, opp.deal_currency) AS OppWinValue GROUP BY opportunity_id ) as pipeline", nativeQuery = true)
	BigDecimal getTotalPipelineByUser(@Param("userId") String userId,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	@Query(value = "select opp.opportunity_id, opp.deal_currency, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / "
			+ " (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) AS PRIMARY_BID_VALUE, "
			+ " from opportunity_t OPP join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id "
			+ " left outer join bid_office_group_owner_link_t bofg on bidt.bid_id = bofg.bid_id "
			+ " join geography_mapping_t GMT on GMT.geography = GCMT.geography "
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp "
			+ " where opp.digital_deal_value <> 0 and sales_stage_code = '9' and (OPP.opportunity_owner = (:userId) or (bofg.bid_office_group_owner = (:userId))) "
			+ " and deal_closure_date between (:fromDate) and (:toDate) "
			+ " GMT.geography in (:geoList) or ('') in (:geoList) and SSMT.display_sub_sp in (:serviceLines) or ('') in (:serviceLines)"
			+ "group by opp.opportunity_id, opp.deal_currency ", nativeQuery = true)
	List<Object[]> getOpportunityWinsForReportByOpportunityOrBidOwner(
			@Param("userId") String userId, 
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	
	@Query(value = " select OPP.opportunity_id, opp.deal_currency, sum((digital_deal_value * (select conversion_rate from "
			+ " beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) AS SALES_VALUE "
			+ " from opportunity_t OPP join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id "
			+ "join geography_mapping_t GMT on GMT.geography = GCMT.geography "
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp "
			+ " where opp.digital_deal_value <> 0 and sales_stage_code = '9' and OSSLT.sales_support_owner = "
			+ " (:userId) and deal_closure_date between (:fromDate) and (:toDate) "
			+ "group by OPP.opportunity_id, opp.deal_currency ", nativeQuery = true)
	List<Object[]> getOpportunityWinsForReportBySalesSupportOwner(
			@Param("userId") String userId, 
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
		
	@Query(value = "select count(*), sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name= deal_currency)) /(select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) from opportunity_t  where opportunity_id  in ( "
			+ " SELECT distinct(opportunity_id) FROM (select opp.opportunity_id from opportunity_t OPP JOIN geography_country_mapping_t GCMT on (GCMT.country = OPP.country and (GCMT.geography in (:geoList) or ('') in (:geoList)) " 
			+ " and (Opp.country in (:countryList) or ('') in (:countryList))) join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " join customer_master_t CMT on opp.customer_id = CMT.customer_id "
			+ " join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList))) "
			+ " join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp in (:serviceLines) or ('') in (:serviceLines))) where OPP.opportunity_owner = (:userId) and OPP.sales_stage_code in (:salesStageCodes) and ((OPP.sales_stage_code > 8 and deal_closure_date between (:fromDate) and (:toDate)) or OPP.sales_stage_code < 9) group by opportunity_owner ,opp.opportunity_id, opp.deal_currency "
			+ " union select opp.opportunity_id from opportunity_t OPP join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id "
			+ " join bid_office_group_owner_link_t bofg on bidt.bid_id = bofg.bid_id join customer_master_t CMT on opp.customer_id = CMT.customer_id "
			+ " join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList))) "
			+ " join geography_country_mapping_t GCMT on (GCMT.country = OPP.country and (GCMT.geography in (:geoList) or ('') in (:geoList)) and (Opp.country in (:countryList) or ('') in (:countryList))) join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp in (:serviceLines) or ('') in (:serviceLines))) "
			+ " where sales_stage_code in (:salesStageCodes) and bofg.bid_office_group_owner = (:userId) and ((OPP.sales_stage_code > 8 and deal_closure_date between (:fromDate) and (:toDate)) or OPP.sales_stage_code < 9) group by opportunity_owner ,opp.opportunity_id, opp.deal_currency "
			+ " union select OPP.opportunity_id from opportunity_t OPP join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id "
			+ " join customer_master_t CMT on opp.customer_id = CMT.customer_id join iou_customer_mapping_t ICM on (CMT.iou = ICM.iou AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList))) JOIN geography_country_mapping_t GCMT on (GCMT.country = OPP.country and (GCMT.geography in (:geoList) or ('') in (:geoList) ) and (Opp.country in (:countryList) or ('') in  (:countryList))) join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id join sub_sp_mapping_t SSMT on (ssl.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp in (:serviceLines) or ('') in (:serviceLines))) where sales_stage_code in (:salesStageCodes) and OSSLT.sales_support_owner = (:userId) and ((OPP.sales_stage_code > 8 and deal_closure_date between (:fromDate) and (:toDate)) or OPP.sales_stage_code < 9) group by sales_support_owner, OPP.opportunity_id, opp.deal_currency) AS bdmOppDealValue )", nativeQuery = true )
	Object[][] getOpportunityCountAndDealValueByUser(
			@Param("userId") String userId, 
			@Param("salesStageCodes") List<Integer> salesStageCodes, 
			@Param("geoList") List<String> geoList, 
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines, 
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, 
			@Param("iouList") List<String> iouList);
		
	@Query(value = "(select distinct(OPP.*) as opportunity from opportunity_t OPP join geography_country_mapping_t GCMT on GCMT.country=OPP.country join geography_mapping_t GMT on GMT.geography = GCMT.geography "
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp join customer_master_t CMT on opp.customer_id = CMT.customer_id " 
			+ " where sales_stage_code in (:salesStageCodes) and (OPP.opportunity_owner in (:userIds)) "
			+ " AND ((OPP.sales_stage_code between 0 and 8) OR (OPP.deal_closure_date between (:fromDate) AND (:toDate))) "
			+ " AND (GMT.geography IN (:geoList) OR ('') in (:geoList)) AND (OPP.country IN (:countryList) OR ('') in (:countryList)) AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))) "
			+ " UNION (select distinct(OPP.*) as opportunity from opportunity_t OPP join geography_country_mapping_t GCMT on GCMT.country=OPP.country join geography_mapping_t GMT on GMT.geography = GCMT.geography "
			+ " join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id join bid_office_group_owner_link_t bofg on bidt.bid_id = bofg.bid_id " 
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id " 
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp join customer_master_t CMT on opp.customer_id = CMT.customer_id "
			+ " where sales_stage_code in (:salesStageCodes) and (bofg.bid_office_group_owner in (:userIds)) "
			+ " AND ((OPP.sales_stage_code between 0 and 8) OR (OPP.deal_closure_date between (:fromDate) AND (:toDate))) "
			+ " AND (GMT.geography IN (:geoList) OR ('') in (:geoList)) AND (OPP.country IN (:countryList) OR ('') in (:countryList)) AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines))) "
			+ " UNION (select distinct(opp.*) as opportunity from opportunity_t OPP join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id "
			+ " join geography_country_mapping_t GCMT on GCMT.country=OPP.country join geography_mapping_t GMT on GMT.geography = GCMT.geography " 
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp join customer_master_t CMT on opp.customer_id = CMT.customer_id " 
			+ " where sales_stage_code in (:salesStageCodes) and OSSLT.sales_support_owner in (:userIds) "
			+ " AND ((OPP.sales_stage_code between 0 and 8) OR (OPP.deal_closure_date between (:fromDate) AND (:toDate))) "
			+ " AND (GMT.geography IN (:geoList) OR ('') in (:geoList)) AND (OPP.country IN (:countryList) OR ('') in (:countryList)) AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)))" ,nativeQuery = true)
	List<OpportunityT> getBDMSupervisorOpportunities(
			@Param("userIds") List<String> userIds, 
			@Param("salesStageCodes") List<Integer> salesStageCodes, 
			@Param("geoList") List<String> geoList, 
			@Param("serviceLines") List<String> serviceLines, 
			@Param("countryList") List<String> countryList, 
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	/**
	 * This method used to retrieve list of opportunities by opportunity ids
	 * @param opportunityIds
	 * @return
	 */
	@Query(value = "select distinct opp.* from opportunity_t opp where opportunity_id in (:opportunityIds)",nativeQuery = true)
	List<OpportunityT> findByOpportunityIds(
			@Param("opportunityIds") List<String> opportunityIds);

	/**
	 * @param users
	 * @param salesStage
	 * @param geoList
	 * @param serviceLinesList
	 * @param countryList
	 * @param fromDate
	 * @param toDate
	 * @param iouList 
	 * @return
	 */
	@Query(value = "select user_id, opportunity_id from ((select distinct user_id, (OPP.*) as opportunity from opportunity_t OPP "
			+ " join user_t USR on (USR.user_id=OPP.opportunity_owner and (OPP.opportunity_owner in (:userIds))) "
			+ " join geography_country_mapping_t GCMT on GCMT.country=OPP.country join geography_mapping_t GMT on GMT.geography = GCMT.geography left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id " 
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp "
			+ " join customer_master_t CMT on opp.customer_id = CMT.customer_id join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " where sales_stage_code in (:salesStageCodes) AND ((OPP.sales_stage_code between 0 and 8) OR (OPP.deal_closure_date between (:fromDate) AND (:toDate))) AND (GMT.geography IN (:geoList) OR ('') in (:geoList)) " 
			+ " AND (OPP.country IN (:countryList) OR ('') in (:countryList)) AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)) AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList))) "
			+ " UNION (select distinct user_id, (OPP.*) as opportunity from opportunity_t OPP join geography_country_mapping_t GCMT on GCMT.country=OPP.country " 
			+ " join geography_mapping_t GMT on GMT.geography = GCMT.geography join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id "
			+ " join bid_office_group_owner_link_t bofg on bidt.bid_id = bofg.bid_id join user_t USR on (USR.user_id=OPP.opportunity_owner and (bofg.bid_office_group_owner in (:userIds))) "
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp " 
			+ " join customer_master_t CMT on opp.customer_id = CMT.customer_id join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " where sales_stage_code in (:salesStageCodes) and ((OPP.sales_stage_code between 0 and 8) OR (OPP.deal_closure_date between (:fromDate) AND (:toDate))) AND (GMT.geography IN (:geoList) OR ('') in (:geoList)) "
			+ " AND (OPP.country IN (:countryList) OR ('') in (:countryList)) AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)) AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList))) "
			+ " UNION (select distinct sales_support_owner, (opp.*) as opportunity from opportunity_t OPP join opportunity_sales_support_link_t OSSLT "
			+ " on OSSLT.opportunity_id=OPP.opportunity_id join user_t USR on (USR.user_id=OPP.opportunity_owner and (OSSLT.sales_support_owner in (:userIds))) "
			+ " join geography_country_mapping_t GCMT on GCMT.country=OPP.country join geography_mapping_t GMT on GMT.geography = GCMT.geography left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id " 
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp "
			+ " join customer_master_t CMT on opp.customer_id = CMT.customer_id join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " where sales_stage_code in (:salesStageCodes) and ((OPP.sales_stage_code between 0 and 8) OR (OPP.deal_closure_date between (:fromDate) AND (:toDate))) AND (GMT.geography IN (:geoList) OR ('') in (:geoList)) " 
			+ " AND (OPP.country IN (:countryList) OR ('') in (:countryList)) AND (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)) AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList)))) as bdmUserAndOppId order by user_id",nativeQuery=true)
	List<Object[]> getBDMAndOpportunities(@Param("userIds") List<String> userIds, 
			@Param("salesStageCodes") List<Integer> salesStageCodes, 
			@Param("geoList") List<String> geoList, 
			@Param("serviceLines") List<String> serviceLines, 
			@Param("countryList") List<String> countryList, 
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, 
			@Param("iouList") List<String> iouList);

	
	/**
	* This Method is used to get opportunity name for the given connectId 
	* @param connectId
	* @return
	*/
	@Query(value ="select opportunity_name from opportunity_t OPP "
			+ "join connect_opportunity_link_id_t COPLT on OPP.opportunity_id=COPLT.opportunity_id where connect_id=?1", nativeQuery=true)
	List<String> findLinkOpportunityByConnectId(String connectId);
	
	
	@Modifying
	@Query(value = "update opportunity_t set sales_stage_code=(select sales_stage_code from opportunity_timeline_history_t "
			+ "where opportunity_id=(:opportunityId) and sales_stage_code<>12 order by updated_datetime desc limit 1) "
			+ "where opportunity_id=(:opportunityId) ",nativeQuery=true)
	int reopenOpportunity(@Param("opportunityId") String opportunityId);
	
	/**
	 * Get Opportunities Won by users
	 * 
	 * @param users
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	@Query(value ="select * from opportunity_t  OPP where OPP.opportunity_id in "
			+ "((select opportunity_id from opportunity_t where opportunity_owner in (:users)) "
			+ "union (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (:users)) "
			+ "union (select opportunity_id from bid_details_t BDT where BDT.bid_id in "
			+ "(select bid_id from bid_office_group_owner_link_t where bid_office_group_owner in (:users)))) "
			+ "AND ((OPP.sales_stage_code = 9) AND (OPP.deal_closure_date between (:fromDate) AND (:toDate))) "
			+ "ORDER By OPP.country ASC", nativeQuery=true)
	List<OpportunityT> getOpportunityWinsForUsers(@Param("users")List<String> users, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
	
	
	
	/**
	 * To fetch the opportunities based on the specific user and the subordinates of the user
	 * @param fromDateTs
	 * @param toDateTs
	 * @param searchedUserId
	 * @return
	 */
	@Query(value ="select * from opportunity_t  OPP where OPP.opportunity_id in"
			+"((select opportunity_id from opportunity_t where opportunity_owner in (:searchedUserIdList)) union"
					+"(select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (:searchedUserIdList)) union"
					+"(select opportunity_id from bid_details_t BDT where BDT.bid_id in (select bid_id from bid_office_group_owner_link_t where bid_office_group_owner in (:searchedUserIdList))))"
					+"AND ((OPP.sales_stage_code between 0 and 8) OR (OPP.deal_closure_date between (:fromDate) AND (:toDate)))",nativeQuery=true)
	List<OpportunityT> getAllOpportunitiesBySearchedIdQuery(@Param("searchedUserIdList") List<String> searchedUserId,@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	
}