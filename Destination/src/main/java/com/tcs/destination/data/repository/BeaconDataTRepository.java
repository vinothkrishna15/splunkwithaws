package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BeaconDataT;

@Repository
public interface BeaconDataTRepository extends
		CrudRepository<BeaconDataT, String> {

	@Query(value = "select BDT.quarter, case when sum(BDT.target) is not null then sum(BDT.target) else '0.0' end as target from beacon_data_t BDT "
			+ "join geography_mapping_t GMT on BDT.beacon_geography = GMT.geography and (GMT.display_geography = ?3 or ?3= '') "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou and (ICMT.display_iou = ?4 or ?4 = '') "
			+ "join beacon_customer_mapping_t BCMT on BDT.beacon_customer_name = BCMT.beacon_customer_name and (BCMT.customer_name = ?5 or ?5 = '') "
			+ "where BDT.financial_year = ?1 and (BDT.quarter = ?2 or ?2 = '') group by BDT.quarter order by BDT.quarter asc", nativeQuery=true)
	List<Object[]> findTargetRevenue(String financialYear, String quarter,
			String geography, String iou, String customerName);

}
