package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ActualRevenuesDataT;

public interface PerformanceReportRepository extends
		CrudRepository<ActualRevenuesDataT, String> {

	@Query(value = "select distinct ICMT.display_iou as displayIOU, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from iou_customer_mapping_t ICMT left outer join"
			+ " (select ICMT.display_iou as displayIOU, sum(ARDT.revenue) as actualRevenue"
			+ " from iou_customer_mapping_t ICMT join actual_revenues_data_t ARDT on ICMT.iou = ARDT.finance_iou"
			+ " and ARDT.financial_year = ?1 and (ARDT.quarter = ?2 or ?2 = '')"
			+ " join geography_mapping_t GMT on ARDT.finance_geography = GMT.geography and (GMT.display_geography = ?3 or ?3 = '')"
			+ " join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = ?4 or ?4 = '')"
			+ " group by ICMT.display_iou"
			+ " order by actualRevenue desc) Result on ICMT.display_iou = Result.displayIOU order by revenue desc", nativeQuery=true)
	  public List<Object[]> getRevenuesByIOU(String financialYear, String quarter, String geography, String serviceLine);
	  
	  @Query(value="select distinct SSMT.display_sub_sp as displaySubSp, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			  +" from sub_sp_mapping_t SSMT left outer join" 
			  +" (select SSMT.display_sub_sp as displaySubSp, sum(ARDT.revenue) as actualRevenue" 
			  +" from sub_sp_mapping_t SSMT join actual_revenues_data_t ARDT on SSMT.sub_sp = ARDT.sub_sp"
			  +" and ARDT.financial_year = ?1 and (ARDT.quarter = ?2 or ?2 = '')"
			  +" join geography_mapping_t GMT on ARDT.finance_geography = GMT.geography and (GMT.display_geography = ?3 or ?3 = '')"
			  +" join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou and (ICMT.display_iou = ?5 or ?5 = '')"
			  +" join revenue_customer_mapping_t RCMT on ARDT.finance_customer_name = RCMT.finance_customer_name and "
			  +" (RCMT.customer_name = ?4 or ?4 = '')"
			  +" group by SSMT.display_sub_sp"
			  +" order by actualRevenue desc) Result" 
			  +" on SSMT.display_sub_sp = Result.displaySubSp order by revenue desc",nativeQuery=true)
	  public List<Object[]> getRevenuesBySubSp(String financialYear,String quarter,String geography,String customerName,String iou);
	  
	  @Query(value="select distinct GMT.display_geography as displayGeography, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			  + " from geography_mapping_t GMT left outer join"
			  + " (select GMT.display_geography as displayGeography, sum(ARDT.revenue) as actualRevenue"
			  + " from geography_mapping_t GMT join actual_revenues_data_t ARDT on GMT.geography = ARDT.finance_geography" 
			  + " and ARDT.financial_year = ?1 and (ARDT.quarter = ?2 or ?2 = '')"
			  + " join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = ?4 or ?4 = '')"
			  + " join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou and (ICMT.display_iou = ?5 or ?5 = '')"
			  + " join revenue_customer_mapping_t RCMT on ARDT.finance_customer_name = RCMT.finance_customer_name and"
			  + " (RCMT.customer_name = ?3 or ?3 = '')"
			  + " group by GMT.display_geography"
			  + " order by actualRevenue desc) Result"
			  + " on GMT.display_geography = Result.displayGeography order by revenue desc",nativeQuery=true)
	  public List<Object[]> getRevenuesByDispGeo(String financialYear,String quarter,String customer,String subSp,String iou);
	  
	  @Query(value="select GMT.geography, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			  + " from geography_mapping_t GMT left outer join" 
			  + " (select GMT.geography as displayGeography, sum(ARDT.revenue) as actualRevenue" 
			  + " from geography_mapping_t GMT left outer join actual_revenues_data_t ARDT on GMT.geography = ARDT.finance_geography" 
			  + " and (GMT.display_geography = ?6 or ?6 = '')"
			  + " join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = ?4 or ?4 = '')"
			  + " join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou and (ICMT.display_iou = ?5 or ?5 = '')"
			  + " join revenue_customer_mapping_t RCMT on ARDT.finance_customer_name = RCMT.finance_customer_name and (RCMT.customer_name = ?3 or ?3 = '')"
			  + " where ARDT.financial_year = ?1 and (ARDT.quarter = ?2 or ?2 = '')"
			  + " group by GMT.geography"
			  + " order by actualRevenue desc) Result on GMT.geography = Result.displayGeography"
			  + " where GMT.display_geography = ?6 order by revenue desc",nativeQuery=true)
	  public List<Object[]> getRevenuesBySubGeo(String financialYear,String quarter,String customer,String subSp,String iou,String geography);
	  
	  
	  
}
