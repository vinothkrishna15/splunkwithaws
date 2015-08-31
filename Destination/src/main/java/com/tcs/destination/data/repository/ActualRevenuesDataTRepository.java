package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ActualRevenuesDataT;

@Repository
public interface ActualRevenuesDataTRepository extends
		CrudRepository<ActualRevenuesDataT, String> {

	@Query(value = "select ARDT.quarter, case when sum(ARDT.revenue) is not null then sum(ARDT.revenue) else '0.0' end as actual_revenue from actual_revenues_data_t ARDT "
			+ "join geography_mapping_t GMT on ARDT.finance_geography = GMT.geography and (GMT.display_geography = ?3 or ?3 = '') "
			+ "join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou and (ICMT.display_iou = ?4 or ?4 = '') "
			+ "join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp and (SSMT.display_sub_sp = ?6 or ?6 = '') "
			+ "join revenue_customer_mapping_t RCMT on "
			+ "(ARDT.finance_customer_name = RCMT.finance_customer_name and ARDT.finance_geography = RCMT.customer_geography) and "
			+ "(RCMT.customer_name = ?5 or ?5= '')  where ARDT.financial_year = ?1 and (ARDT.quarter = ?2 or ?2 = '') "
			+ "group by ARDT.quarter order by ARDT.quarter asc ", nativeQuery = true)
	List<Object[]> findActualRevenue(String financialYear, String quarter,
			String geography, String iou, String customerName,
			String serviceLine);

	@Query(value = "select RCMT.customer_name,ARDT.quarter,sum(ARDT.revenue) from actual_revenues_data_t ARDT "
			+ "JOIN revenue_customer_mapping_t RCMT on RCMT.finance_customer_name=ARDT.finance_customer_name "
			+ "JOIN geography_mapping_t GMT on ARDT.finance_geography = GMT.geography and (GMT.geography in (:geoList) or ('') in (:geoList)) "
			+ "JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "where upper(ARDT.month) in (:monthList) group by RCMT.customer_name,ARDT.quarter", nativeQuery = true)
	public List<Object[]> getActualRevenuesByQuarter(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("monthList") List<String> monthList);
	
	@Query(value = "select sum(revenue) as top_revenue from (select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue from " 
	+ "(((select RCMT.customer_name, sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT "
	+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name "
 	+ "and RCMT.customer_geography=ARDT.finance_geography)JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou " 
 	+ "JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp "
 	+ "where upper(ARDT.month) in (:monthList) "
  	+ "and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) " 
  	+ "and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
 	+ "group by RCMT.customer_name order by actual_revenue desc) "
 	+ "UNION (select RCMT.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue from projected_revenues_data_t PRDT " 
 	+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name " 
 	+ "and RCMT.customer_geography=PRDT.finance_geography) "
 	+ "JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou " 
 	+ "JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp " 
 	+ "where upper(PRDT.month) in (:monthList) "
 	+ "and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) "
 	+ "and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) " 
 	+ "group by RCMT.customer_name order by projected_revenue desc))) " 
 	+ "as RVNU group by RVNU.customer_name order by revenue desc LIMIT 30) as top_Revenue ", nativeQuery = true)
	public Object[] getTop30CustomersRevenueByQuarter(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("monthList") List<String> monthList);

	@Query(value = "select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue from  "
	+ "((select RCMT.customer_name, sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT " 
	+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name " 
	+ " and RCMT.customer_geography=ARDT.finance_geography) "
	+ "JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
	+ "JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp  "
	+ "where upper(ARDT.month) in (:monthList) "
	+ "and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) "
	+ "and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
	+ "group by RCMT.customer_name order by actual_revenue desc) "
	+ " UNION (select RCMT.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue " 
	+ "from projected_revenues_data_t PRDT " 
	+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name and RCMT.customer_geography=PRDT.finance_geography) "
	+ "JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
	+ "where upper(PRDT.month) in (:monthList) "
	+ "and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) "
	+ "and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
	+ "group by RCMT.customer_name order by projected_revenue desc)) "
	+ " as RVNU group by RVNU.customer_name order by revenue desc ", nativeQuery = true)
	public List<Object[]> getOverAllActualRevenues(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("monthList") List<String> monthList);
	
	@Query(value = "select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue from  "
		+ "((select RCMT.customer_name, sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT " 
		+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name " 
		+ " and RCMT.customer_geography=ARDT.finance_geography) "
		+ "JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
		+ "JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp  "
		+ "where upper(ARDT.month) in (:monthList) "
			+ "and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) "
			+ "and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "group by RCMT.customer_name order by actual_revenue desc) "
		+ " UNION (select RCMT.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue " 
			+ "from projected_revenues_data_t PRDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name and RCMT.customer_geography=PRDT.finance_geography) "
			+ "JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ "where upper(PRDT.month) in (:monthList) "
			+ "and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) "
			+ "and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "group by RCMT.customer_name order by projected_revenue desc)) "
			+ " as RVNU group by RVNU.customer_name order by revenue desc LIMIT 30 ", nativeQuery = true)
	public List<Object[]> getTop30CustomersRevenues(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("monthList") List<String> monthList);
	
	@Query(value = "select sum(actual_revenue) as revenue from ( "
			+ "(select sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name " 
			+ "and RCMT.customer_geography=ARDT.finance_geography)JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou " 
			+ "JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp "
			+ "where upper(ARDT.month) in (:monthList) "
			+ "and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) " 
			+ "and (ICMT.display_iou in (:iouList) or ('') in (:iouList))) "
			+ "UNION (select case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue from projected_revenues_data_t PRDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name "
			+ "and RCMT.customer_geography=PRDT.finance_geography) "
			+ "JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou " 
			+ "JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ "where upper(PRDT.month) in (:monthList) "
			+ "and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) " 
			+ "and (ICMT.display_iou in (:iouList) or ('') in (:iouList)))) as RVNU", nativeQuery = true)
	public Object[] getTotalRevenue(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("monthList") List<String> monthList);
	
	@Query(value = "select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue, RVNU.display_geography from  "
			+ "((select RCMT.customer_name, sum(ARDT.revenue) as actual_revenue, GMT.display_geography from actual_revenues_data_t ARDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name " 
			+ " and RCMT.customer_geography=ARDT.finance_geography) "
			+ " JOIN geography_mapping_t GMT on ARDT.finance_geography = GMT.geography "
			+ " JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ " JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp  "
			+ " where upper(ARDT.month) in (:monthList) "
			+ "and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) "
			+ "and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "group by RCMT.customer_name, GMT.display_geography order by actual_revenue desc) "
			+ " UNION (select RCMT.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue " 
			+ ", GMT.display_geography from projected_revenues_data_t PRDT " 
			+ " JOIN geography_mapping_t GMT on PRDT.finance_geography = GMT.geography"
			+ " JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name and RCMT.customer_geography=PRDT.finance_geography) "
			+ " JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ " where upper(PRDT.month) in (:monthList) "
			+ " and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) "
			+ " and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ " group by RCMT.customer_name, GMT.display_geography order by projected_revenue desc)) "
			+ " as RVNU group by RVNU.customer_name, RVNU.display_geography order by revenue desc ", nativeQuery = true)
	public List<Object[]> getOverAllActualRevenuesByGeo(
			@Param("geoList") List<String> geographyList,
			@Param("iouList")List<String> iouList,
			@Param("monthList") List<String> monthList);
	
	
	@Query(value = "select RVNU.customer_name, RVNU.finance_customer_name, RVNU.display_iou, RVNU.display_geography "
			+ "from ((select RCMT.customer_name, RCMT.finance_customer_name, icmt.display_iou, " 
			+ "gmt.display_geography from actual_revenues_data_t ARDT JOIN revenue_customer_mapping_t RCMT on "
			+ "(RCMT.finance_customer_name = ARDT.finance_customer_name and RCMT.customer_geography=ARDT.finance_geography) "
			+ "JOIN geography_mapping_t GMT on ARDT.finance_geography = GMT.geography "
			+ "JOIN iou_customer_mapping_t ICMT on "
			+ "ARDT.finance_iou = ICMT.iou JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp " 
			+ "where upper(ARDT.month) in (:monthList) "
			+ "and (RCMT.customer_geography in (:geoList) or ('') in (:geoList))"
			+ " and (ICMT.display_iou in (:iouList) or ('') in (:iouList))) "
			+ "UNION (select RCMT.customer_name, RCMT.finance_customer_name, icmt.display_iou, gmt.display_geography "
			+ "from projected_revenues_data_t PRDT JOIN geography_mapping_t GMT on PRDT.finance_geography = GMT.geography "
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name "
			+ "and RCMT.customer_geography=PRDT.finance_geography) JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou " 
			+ "JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ "where upper(PRDT.month) in (:monthList) and (RCMT.customer_geography in (:geoList) or ('') in (:geoList)) " 
			+ "and (ICMT.display_iou in (:iouList) or ('') in (:iouList)))) as RVNU ", nativeQuery = true)
	public List<Object[]> getGroupCustGeoIou(
			@Param("geoList") List<String> geoList,
			@Param("iouList")List<String> iouList,
			@Param("monthList") List<String> monthList);
}
