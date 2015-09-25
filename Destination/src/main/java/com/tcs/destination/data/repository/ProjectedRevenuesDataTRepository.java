package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ProjectedRevenuesDataT;

@Repository
public interface ProjectedRevenuesDataTRepository extends
		CrudRepository<ProjectedRevenuesDataT, String> {

	@Query(value = "select PRDT.quarter, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0.0' end as projected_revenue from projected_revenues_data_t PRDT "
			+ "join geography_mapping_t GMT on PRDT.finance_geography = GMT.geography and (PRDT.finance_geography = (:geography) or (:geography) = '') "
			+ "and (GMT.display_geography = (:displayGeography) or (:displayGeography)='') "
			+ "join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou and (ICMT.display_iou = (:iou) or (:iou) = '') "
			+ "join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp and (SSMT.display_sub_sp = (:serviceLine) or (:serviceLine) = '') "
			+ "join revenue_customer_mapping_t RCMT on "
			+ "(PRDT.finance_customer_name = RCMT.finance_customer_name and PRDT.finance_geography=RCMT.customer_geography) and "
			+ "(RCMT.customer_name  in (:customerName) or ('') in (:customerName) and RCMT.finance_iou =PRDT.finance_iou)  where PRDT.financial_year = (:financialYear) and (PRDT.quarter = (:quarter) or (:quarter) = '') "
			+ "group by PRDT.quarter order by PRDT.quarter asc ", nativeQuery = true)
	List<Object[]> findProjectedRevenue(
			@Param("financialYear") String financialYear,
			@Param("quarter") String quarter,
			@Param("displayGeography") String displayGeography,
			@Param("geography") String geography, @Param("iou") String iou,
			@Param("customerName") List<String> customerName,
			@Param("serviceLine") String serviceLine);

	@Query(value = "select distinct ICMT.display_iou as displayIOU, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from iou_customer_mapping_t ICMT left outer join"
			+ " (select ICMT.display_iou as displayIOU, sum(PRDT.revenue) as actualRevenue"
			+ " from iou_customer_mapping_t ICMT join projected_revenues_data_t PRDT on ICMT.iou = PRDT.finance_iou"
			+ " and PRDT.financial_year = ?1 and (PRDT.quarter = ?2 or ?2 = '')"
			+ " join geography_mapping_t GMT on PRDT.finance_geography = GMT.geography and (PRDT.finance_geography = ?3 or ?3 = '')"
			+ " join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp and (SSMT.display_sub_sp = ?4 or ?4 = '')"
			+ " group by ICMT.display_iou"
			+ " order by actualRevenue desc) Result on ICMT.display_iou = Result.displayIOU order by revenue desc", nativeQuery = true)
	public List<Object[]> getRevenuesByIOU(String financialYear,
			String quarter, String geography, String serviceLine);

	@Query(value = "select distinct SSMT.display_sub_sp as displaySubSp, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from sub_sp_mapping_t SSMT left outer join"
			+ " (select SSMT.display_sub_sp as displaySubSp, sum(PRDT.revenue) as actualRevenue"
			+ " from sub_sp_mapping_t SSMT join projected_revenues_data_t PRDT on SSMT.actual_sub_sp = PRDT.sub_sp"
			+ " and PRDT.financial_year = (:financialYear) and (PRDT.quarter = (:quarter) or (:quarter) = '')"
			+ " join geography_mapping_t GMT on PRDT.finance_geography = GMT.geography and (PRDT.finance_geography = (:geography) or (:geography) = '')"
			+ " and (GMT.display_geography=(:displayGeography) or (:displayGeography) ='')"
			+ " join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " join revenue_customer_mapping_t RCMT on PRDT.finance_customer_name = RCMT.finance_customer_name and "
			+ " (RCMT.customer_name in (:customerName) or ('') in (:customerName)) and RCMT.finance_iou =PRDT.finance_iou and PRDT.finance_geography=RCMT.customer_geography"
			+ " group by SSMT.display_sub_sp"
			+ " order by actualRevenue desc) Result"
			+ " on SSMT.display_sub_sp = Result.displaySubSp order by revenue desc", nativeQuery = true)
	public List<Object[]> getRevenuesBySubSp(
			@Param("financialYear") String financialYear,
			@Param("quarter") String quarter,
			@Param("displayGeography") String displayGeography,
			@Param("geography") String geography,
			@Param("customerName") List<String> customerName, @Param("iou") String iou);

	@Query(value = "select distinct GMT.display_geography as displayGeography, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from geography_mapping_t GMT left outer join"
			+ " (select GMT.display_geography as displayGeography, sum(PRDT.revenue) as actualRevenue"
			+ " from geography_mapping_t GMT join projected_revenues_data_t PRDT on GMT.geography = PRDT.finance_geography"
			+ " and PRDT.financial_year = (:financialYear) and (PRDT.quarter = (:quarter) or (:quarter) = '')"
			+ " join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp and (SSMT.display_sub_sp = (:subSp) or (:subSp) = '')"
			+ " join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " join revenue_customer_mapping_t RCMT on PRDT.finance_customer_name = RCMT.finance_customer_name and"
			+ " (RCMT.customer_name in (:customer) or ('') in (:customer)) and RCMT.finance_iou =PRDT.finance_iou and PRDT.finance_geography=RCMT.customer_geography"
			+ " group by GMT.display_geography"
			+ " order by actualRevenue desc) Result"
			+ " on GMT.display_geography = Result.displayGeography order by revenue desc", nativeQuery = true)
	public List<Object[]> getRevenuesByDispGeo(@Param("financialYear") String financialYear,
			@Param("quarter") String quarter, @Param("customer") List<String> customer,
			@Param("subSp") String subSp, @Param("iou") String iou);

	@Query(value = "select GMT.geography, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from geography_mapping_t GMT left outer join"
			+ " (select GMT.geography as displayGeography, sum(PRDT.revenue) as actualRevenue"
			+ " from geography_mapping_t GMT left outer join projected_revenues_data_t PRDT on GMT.geography = PRDT.finance_geography"
			+ " and (PRDT.finance_geography = (:geography) or (:geography) = '')"
			+ " join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp and (SSMT.display_sub_sp = (:subSp) or (:subSp) = '')"
			+ " join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " join revenue_customer_mapping_t RCMT on PRDT.finance_customer_name = RCMT.finance_customer_name and (RCMT.customer_name in (:customer) or ('') in (:customer))"
			+ " and RCMT.finance_iou =PRDT.finance_iou and PRDT.finance_geography=RCMT.customer_geography"
			+ " where PRDT.financial_year = (:financialYear) and (PRDT.quarter = (:quarter) or (:quarter) = '')"
			+ " group by GMT.geography"
			+ " order by actualRevenue desc) Result on GMT.geography = Result.displayGeography"
			+ " where GMT.display_geography = (:geography) order by revenue desc", nativeQuery = true)
	public List<Object[]> getRevenuesBySubGeo(@Param("financialYear") String financialYear,
			@Param("quarter") String quarter, @Param("customer") List<String> customer, 
			@Param("subSp") String subSp, @Param("iou") String iou,
			@Param("geography") String geography);

	@Query(value = "select RCMT.customer_name,PRDT.quarter,sum(PRDT.revenue) from projected_revenues_data_t PRDT "
			+ "JOIN revenue_customer_mapping_t RCMT on RCMT.finance_customer_name=PRDT.finance_customer_name "
			+ "and RCMT.customer_geography = PRDT.finance_geography and RCMT.finance_iou =PRDT.finance_iou "
			+ "JOIN geography_mapping_t GMT on PRDT.finance_geography = GMT.geography and (PRDT.finance_geography in (:geoList) or ('') in (:geoList)) "
			+ "join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "where upper(PRDT.month) in (:monthList) and RCMT.customer_name not like 'UNKNOWN%' group by RCMT.customer_name,PRDT.quarter", nativeQuery = true)
	public List<Object[]> getProjectedRevenuesByQuarter(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("monthList") List<String> monthList);

	@Query(value = "select case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as revenue_sum from projected_revenues_data_t PRDT "
			+ "JOIN geography_mapping_t GMT on PRDT.finance_geography = GMT.geography and (PRDT.finance_geography in (:geoList) or ('') in (:geoList)) "
			+ "join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "where upper(PRDT.month) in (:monthList)", nativeQuery = true)
	public Object[] getTotalProjectedRevenuesByQuarter(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("monthList") List<String> monthList);

	@Query(value = "select RCMT.customer_name,case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as revenue_sum from projected_revenues_data_t PRDT "
			+ " JOIN revenue_customer_mapping_t RCMT on RCMT.finance_customer_name=PRDT.finance_customer_name and RCMT.finance_iou =PRDT.finance_iou and PRDT.finance_geography=RCMT.customer_geography "
			+ "JOIN geography_mapping_t GMT on PRDT.finance_geography = GMT.geography and (PRDT.finance_geography in (:geoList) or ('') in (:geoList)) "
			+ "join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "where upper(PRDT.month) in (:monthList) "
			+ "group by RCMT.customer_name "
			+ "order by revenue_sum desc LIMIT 30", nativeQuery = true)
	public List<Object[]> getProjectedRevenues(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("monthList") List<String> monthList);

	@Query(value = "select upper(PRDT.month), case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0.0' end as projected_revenue from projected_revenues_data_t PRDT "
			+ "join geography_mapping_t GMT on PRDT.finance_geography = GMT.geography  and (GMT.geography=(:geography) or (:geography)='')"
			+ "and (GMT.display_geography = (:displayGeography) or (:displayGeography)='') "
			+ "join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou and (ICMT.display_iou = (:iou) or (:iou) = '') "
			+ "join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp and (SSMT.display_sub_sp = (:serviceLine) or (:serviceLine) = '') "
			+ "join revenue_customer_mapping_t RCMT on "
			+ "(PRDT.finance_customer_name = RCMT.finance_customer_name and PRDT.finance_geography=RCMT.customer_geography and RCMT.finance_iou =PRDT.finance_iou) and "
			+ "(RCMT.customer_name  in (:customerName) or ('') in (:customerName))  where PRDT.financial_year = (:financialYear) and (PRDT.quarter = (:quarter) or (:quarter) = '') "
			+ "group by PRDT.month order by PRDT.month asc ", nativeQuery = true)
	public List<Object[]> findProjectedRevenueByQuarter(
			@Param("financialYear") String financialYear,
			@Param("quarter") String quarter,
			@Param("displayGeography") String displayGeography,
			@Param("geography") String geography, @Param("iou") String iou,
			@Param("customerName") List<String> customerName,
			@Param("serviceLine") String serviceLine);

	@Query(value = "select Result.country, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue from geography_mapping_t GMT "
			+ "left outer join "
			+ "(select (PRDT.client_country) as country,(GCMT.geography) as geography, sum(PRDT.revenue) as actualRevenue from geography_country_mapping_t GCMT "
			+ "left outer join projected_revenues_data_t PRDT on (GCMT.geography=PRDT.finance_geography)"
			+ "join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp and (SSMT.display_sub_sp = (:subSp) or (:subSp) = '') "
			+ "join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou and (ICMT.display_iou = (:iou) or (:iou) = '') "
			+ "join revenue_customer_mapping_t RCMT on PRDT.finance_customer_name = RCMT.finance_customer_name and (RCMT.customer_name in (:customer) or ('') in (:customer)) "
			+ "and RCMT.finance_iou =PRDT.finance_iou and PRDT.finance_geography=RCMT.customer_geography "
			+ "where PRDT.financial_year = (:financialYear) and (PRDT.quarter = (:quarter) or (:quarter) = '') "
			+ "group by PRDT.client_country,GCMT.geography order by actualRevenue desc) Result on GMT.geography = Result.geography where GMT.geography = (:geography) order by revenue desc", nativeQuery = true)
	public List<Object[]> getRevenuesByCountry(@Param("financialYear") String financialYear,
			@Param("quarter") String quarter, @Param("customer") List<String> customer,
			@Param("subSp") String subSp, @Param("iou") String iou,
			@Param("geography") String geography);

}
