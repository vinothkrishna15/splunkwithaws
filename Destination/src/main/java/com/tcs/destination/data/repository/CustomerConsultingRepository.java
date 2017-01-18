/**
 * 
 */
package com.tcs.destination.data.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tcs.destination.bean.OpportunityT;

/**
 * @author tcs2
 *
 */
public interface CustomerConsultingRepository extends
		CrudRepository<OpportunityT, Serializable> {

	@Query(value = "select  sum(revenue * (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name= 'INR') /  (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name = ('USD')) ) , group_customer_name from actual_revenues_data_t ardt "
			+ "join revenue_customer_mapping_t rcmt on ardt.revenue_customer_map_id = rcmt.revenue_customer_map_id "
			+ "join customer_master_t cmt on rcmt.customer_id = cmt.customer_id "
			+ "where upper(ardt.sub_sp) like upper('%Consulting%') AND upper(ardt.category) like upper('%REVENUE%') AND ardt.financial_year = ?1 "
			+ "group by group_customer_name", nativeQuery = true)
	List<Object[]> findSumOfRevenueWithGroupCustomerByRevenue(
			String financialYear);

	@Query(value = "select  sum(revenue * (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name= 'INR') /  (select conversion_rate from beacon_convertor_mapping_t "
			+ "where currency_name = ('USD')) ), group_customer_name from actual_revenues_data_t ardt "
			+ "join revenue_customer_mapping_t rcmt on ardt.revenue_customer_map_id = rcmt.revenue_customer_map_id "
			+ "join customer_master_t cmt on rcmt.customer_id = cmt.customer_id "
			+ "where upper(ardt.sub_sp) like upper('%Consulting%') AND upper(ardt.category) like upper('%cost%') AND ardt.financial_year = ?1 "
			+ "group by group_customer_name", nativeQuery = true)
	List<Object[]> findSumOfRevenueWithGroupCustomerByCost(String financialYear);

	@Query(value = "select month, count(distinct group_customer_name) from actual_revenues_data_t ardt "
			+ "join revenue_customer_mapping_t rcmt on ardt.revenue_customer_map_id = rcmt.revenue_customer_map_id "
			+ "join customer_master_t cmt on rcmt.customer_id = cmt.customer_id "
			+ "where upper(ardt.sub_sp) like upper('%Consulting%') AND upper(ardt.category) like upper('%revenue%') AND ardt.financial_Year = ?1 "
			+ "group by ardt.month", nativeQuery = true)
	List<Object[]> findMonthwiseRevenueForConsulting(String financialYear);

	@Query(value = "select max(to_date(concat('01-', month), 'DD-mon-yy')) from actual_revenues_data_t ardt "
			+ "where ardt.financial_year = ?1", nativeQuery = true)
	Date findLastModifiedDate(String financialYear);

	@Query(value = "select sum(deal_value_usd_converter(OPP.digital_deal_value, OPP.deal_currency)) as dealValueSum, OPP.deal_currency,CMT.group_customer_name from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "where sales_stage_code in (9) AND upper(OSSL.sub_sp) like upper('%Consulting%') AND OPP.deal_closure_date between (:fromDate) and (:toDate) "
			+ "group by group_customer_name, deal_currency", nativeQuery = true)
	List<Object[]> findSumOfDealvalueWithGroupCustomerByWins(
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "select sum(deal_value_usd_converter(OPP.digital_deal_value, OPP.deal_currency)) as dealValueSum, OPP.deal_currency,CMT.group_customer_name from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "where sales_stage_code in (4,5,6,7,8) AND upper(OSSL.sub_sp) like upper('%Consulting%') AND OPP.deal_closure_date between (:fromDate) and (:toDate) "
			+ "group by group_customer_name, deal_currency", nativeQuery = true)
	List<Object[]> findSumOfDealvalueWithGroupCustomerByQualified(
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

}