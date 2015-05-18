package com.tcs.destination.data.repository;

import java.util.List;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ActualRevenuesDataT;

@Repository
public interface ActualRevenuesDataTRepository extends
		CrudRepository<ActualRevenuesDataT, String> {

	@Query(value="select ARDT.quarter, case when sum(ARDT.revenue) is not null then sum(ARDT.revenue) else '0.0' end as actual_revenue from actual_revenues_data_t ARDT "
			+ "join geography_mapping_t GMT on ARDT.finance_geography = GMT.geography and (GMT.display_geography = ?3 or ?3 = '') "
			+ "join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou and (ICMT.display_iou = ?4 or ?4 = '') "
			+ "join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.sub_sp and (SSMT.display_sub_sp = ?6 or ?6 = '') "
			+ "join revenue_customer_mapping_t RCMT on ARDT.finance_customer_name = RCMT.finance_customer_name and "
			+ "(RCMT.customer_name = ?5 or ?5= '')  where ARDT.financial_year = ?1 and (ARDT.quarter = ?2 or ?2 = '') "
			+ "group by ARDT.quarter order by ARDT.quarter asc ", nativeQuery=true)
	List<Object[]> findActualRevenue(
			String financialYear,
			String quarter,
			String geography,
			String iou,
			String customerName,
			String serviceLine);

}
