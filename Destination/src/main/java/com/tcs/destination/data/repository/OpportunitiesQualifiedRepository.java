/**
 * 
 */
package com.tcs.destination.data.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.dto.QualifiedPipelineDTO;

/**
 * @author tcs2
 *
 */
public interface OpportunitiesQualifiedRepository extends
		CrudRepository<BidDetailsT, Serializable> {

	@Query(value = "select OPP.sales_stage_code,count (distinct OPP.opportunity_id), sum(digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name= OPP.deal_currency) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) "
			+ "as ConvertedValue from opportunity_t OPP "
			+ "where sales_stage_code in (4,5,6,7,8) group by sales_stage_code order by sales_stage_code ", nativeQuery = true)
	List<Object[]> findAllQualifiedPipelineOpportunities();

	@Query(value = "select sales_stage_code, count(BDT.opportunity_id) from bid_details_t BDT "
			+ "join opportunity_t OPP on OPP.opportunity_id = BDT.opportunity_id "
			+ "where OPP.sales_stage_code in (4,5,6,7,8) AND BDT.bid_id = (select bid_id from bid_details_t where upper (bid_request_type) = upper('proactive') "
			+ "and opportunity_id=OPP.opportunity_id order by modified_datetime DESC limit 1) "
			+ "group by sales_stage_code order by sales_stage_code", nativeQuery = true)
	List<Object[]> findOpportunitiesCountByProactiveType();

	@Query(value = "select OPP.sales_stage_code,count (((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name= OPP.deal_currency) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) / '1000000') > '1.0') "
			+ "as oneMillionCount from opportunity_t OPP "
			+ "where sales_stage_code in (4,5,6,7,8) AND ((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name= OPP.deal_currency) /  (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name = ('USD'))) / '1000000' > '1.0') ='t' "
			+ "group by sales_stage_code order by sales_stage_code ", nativeQuery = true)
	List<Object[]> findOneMillionQualifiedPipelineOpportunities();

	@Query(value = "select OPP.sales_stage_code,count(OPP.opportunity_id) as OpporCount, sum(digital_deal_value * (select conversion_rate "
			+ "from beacon_convertor_mapping_t "
			+ "where currency_name= OPP.deal_currency) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD')) ) "
			+ "from opportunity_t OPP join user_t USRT on USRT.user_id = OPP.opportunity_owner "
			+ "where sales_stage_code in (4,5,6,7,8) AND (USRT.user_group) in (?1) "
			+ "group by sales_stage_code order by sales_stage_code ", nativeQuery = true)
	List<Object[]> findSalesQualifiedPipelineOpportunities(
			List<String> userGroup);

	@Query(value = "select OPP.sales_stage_code, count(distinct BDT.opportunity_id) from bid_details_t BDT "
			+ "join opportunity_t OPP on OPP.opportunity_id = BDT.opportunity_id "
			+ "join user_t USRT on USRT.user_id = OPP.opportunity_owner "
			+ "where OPP.sales_stage_code between '4' and '8' AND BDT.bid_id = (select bid_id from bid_details_t where upper (bid_request_type) = upper('proactive') "
			+ "and opportunity_id=OPP.opportunity_id order by modified_datetime DESC limit 1) "
			+ "AND (USRT.user_group) in (?1) "
			+ "group By OPP.sales_stage_code order by OPP.sales_stage_code", nativeQuery = true)
	List<Object[]> findSalesOpportunitiesCountByProactiveType(
			List<String> userGroup);

	@Query(value = "select OPP.sales_stage_code,count (((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name= OPP.deal_currency) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) / '1000000') > '1.0') "
			+ "as oneMillionCount from opportunity_t OPP "
			+ "join user_t USRT on USRT.user_id = OPP.opportunity_owner "
			+ "where sales_stage_code in (4,5,6,7,8) AND ((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name= OPP.deal_currency) /  (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name = ('USD'))) / '1000000' > '1.0') ='t' "
			+ "AND (USRT.user_group) in (?1) "
			+ "group by sales_stage_code order by sales_stage_code ", nativeQuery = true)
	List<Object[]> findSalesOneMillionQualifiedPipelineOpportunities(
			List<String> userGroup);
}
