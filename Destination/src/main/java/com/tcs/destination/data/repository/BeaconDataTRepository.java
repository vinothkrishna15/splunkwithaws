package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BeaconDataT;

@Repository
public interface BeaconDataTRepository extends
		CrudRepository<BeaconDataT, String> {

	@Query(value = "select BDT.quarter, case when sum(BDT.target) is not null then sum(BDT.target) else '0.0' end as target from beacon_data_t BDT "
			+ "join geography_mapping_t GMT on BDT.beacon_geography = GMT.geography and (GMT.display_geography = ?3 or ?3= '') "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou and (ICMT.display_iou = ?4 or ?4 = '') "
			+ "join beacon_customer_mapping_t BCMT on (BDT.beacon_customer_name = BCMT.beacon_customer_name and BDT.beacon_geography = BCMT.customer_geography) and (BCMT.customer_name = ?5 or ?5 = '') "
			+ "where BDT.financial_year = ?1 and (BDT.quarter = ?2 or ?2 = '') group by BDT.quarter order by BDT.quarter asc", nativeQuery = true)
	List<Object[]> findTargetRevenue(String financialYear, String quarter,
			String geography, String iou, String customerName);

	@Query(value = "select BCMT.customer_name,BDT.quarter,sum(BDT.target) from beacon_data_t BDT "
			+ "JOIN beacon_customer_mapping_t BCMT on BCMT.beacon_customer_name=BDT.beacon_customer_name "
			+ "JOIN geography_mapping_t GMT on BDT.beacon_geography = GMT.geography and (GMT.geography in (:geoList) or ('') in (:geoList)) "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "where BDT.quarter in (:quarterList) group by BCMT.customer_name,BDT.quarter", nativeQuery = true)
	public List<Object[]> getTargetByQuarter(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("quarterList") List<String> quarterList);
	
	@Query(value = "select sum(BDT.target) from beacon_data_t BDT "
			+ "JOIN geography_mapping_t GMT on BDT.beacon_geography = GMT.geography and (GMT.geography in (:geoList) or ('') in (:geoList)) "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "where BDT.quarter in (:quarterList)", nativeQuery = true)
	public Object[] getTotalTargetByQuarter(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("quarterList") List<String> quarterList);
	
	@Query(value = "select BCMT.customer_name,sum(BDT.target) as revenue_sum from beacon_data_t BDT  "
			+ "JOIN beacon_customer_mapping_t BCMT on BCMT.beacon_customer_name=BDT.beacon_customer_name "
			+ "JOIN geography_mapping_t GMT on BDT.beacon_geography = GMT.geography and (GMT.geography in (:geoList) or ('') in (:geoList)) "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "where BDT.quarter in (:quarterList) "
//			+ "and  BDT.beacon_customer_name <>'' "
			+ "group by BCMT.customer_name " 
			+ "order by revenue_sum desc", nativeQuery = true)
	public List<Object[]> getTargetRevenueByQuarter(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("quarterList") List<String> quarterList);
	
//	@Query(value = "select ICMT.display_iou,GMT.display_geography,BCMT.customer_name from beacon_data_t BDT "
//			+ "JOIN beacon_customer_mapping_t BCMT on BCMT.beacon_customer_name=BDT.beacon_customer_name "
//			+ "JOIN geography_mapping_t GMT on BDT.beacon_geography = GMT.geography "
//			+ "JOIN iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou "
//			+ "where BCMT.customer_name = ?1 LIMIT 1", nativeQuery = true)
//	public Object[][] getGeographyAndIouByCustomer(String customerName);
	
	@Query(value = "select BCMT.customer_name,sum(BDT.target) as revenue_sum from beacon_data_t BDT  "
			+ "JOIN beacon_customer_mapping_t BCMT on BCMT.beacon_customer_name=BDT.beacon_customer_name "
			+ "JOIN geography_mapping_t GMT on BDT.beacon_geography = GMT.geography and (GMT.geography in (:geoList) or ('') in (:geoList)) "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou and (ICMT.display_iou in (:iouList) or ('') in (:iouList)) "
			+ "where BDT.quarter in (:quarterList) "
//			+ "and  BDT.beacon_customer_name <>'' "
			+ "group by BCMT.customer_name " 
			+ "order by revenue_sum desc", nativeQuery = true)
	public List<Object[]> getOverAllTargetRevenue(
			@Param("iouList") List<String> iouList,
			@Param("geoList") List<String> geoList,
			@Param("quarterList") List<String> quarterList);



}
