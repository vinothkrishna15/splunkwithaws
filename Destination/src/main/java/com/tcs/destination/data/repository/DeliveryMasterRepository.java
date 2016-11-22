package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryMasterT;

@Repository
public interface DeliveryMasterRepository extends JpaRepository<DeliveryMasterT, String> {

	/*------------------Start of delivery smart search queries-------------------*/
	
	/* smart search with opportunity id*/
	@Query(value="SELECT dmt FROM DeliveryMasterT dmt WHERE deliveryStage IN (:stages) AND deliveryCentreId in (:centres) AND "
			+ " UPPER(opportunityId) like UPPER(:term) ORDER BY modifiedDatetime DESC")
	List<DeliveryMasterT> searchByOppIdTermAndCentresAndStages(@Param("term") String queryTerm,
			@Param("centres") List<?> centres, @Param("stages") List<Integer> stages);
	
	@Query(value="SELECT dmt FROM DeliveryMasterT dmt WHERE deliveryStage IN (:stages) AND deliveryMasterId in (:ids) AND "
			+ " UPPER(opportunityId) like UPPER(:term) ORDER BY modifiedDatetime DESC")
	List<DeliveryMasterT> searchByOppIdTermAndIdsAndStages(@Param("term") String queryTerm,
			@Param("ids") List<?> ids, @Param("stages") List<Integer> stages);
	
	/* smart search with customer name*/
	@Query(value="SELECT dmt FROM DeliveryMasterT dmt JOIN dmt.opportunityT opt JOIN opt.customerMasterT cmt WHERE  dmt.deliveryStage IN (:stages) AND dmt.deliveryCentreId in (:centres) AND "
			+ " UPPER(cmt.customerName) like UPPER(:term) ORDER BY dmt.modifiedDatetime DESC")
	List<DeliveryMasterT> searchByCustNameTermAndCentresAndStages(@Param("term") String queryTerm,
			@Param("centres") List<?> centres, @Param("stages") List<Integer> stages);
	
	@Query(value="SELECT dmt FROM DeliveryMasterT dmt JOIN dmt.opportunityT opt JOIN opt.customerMasterT cmt WHERE dmt.deliveryStage IN (:stages) AND dmt.deliveryMasterId in (:ids) AND "
			+ " UPPER(cmt.customerName) like UPPER(:term) ORDER BY dmt.modifiedDatetime DESC")
	List<DeliveryMasterT> searchByCustNameTermAndIdsAndStages(@Param("term") String queryTerm,
			@Param("ids") List<?> ids, @Param("stages") List<Integer> stages);

	/* smart search with delivery centre name*/
	@Query(value="SELECT dmt FROM DeliveryMasterT dmt JOIN dmt.deliveryCentreT dct WHERE dmt.deliveryStage IN (:stages) AND dmt.deliveryCentreId in (:centres) AND "
			+ " UPPER(dct.deliveryCentre) like UPPER(:term) ORDER BY dmt.modifiedDatetime DESC")
	List<DeliveryMasterT> searchByCentreTermAndCentresAndStages(@Param("term") String queryTerm,
			@Param("centres") List<?> centres, @Param("stages") List<Integer> stages);
	
	@Query(value="SELECT dmt FROM DeliveryMasterT dmt JOIN dmt.deliveryCentreT dct WHERE dmt.deliveryStage IN (:stages) AND dmt.deliveryMasterId in (:ids) AND "
			+ " UPPER(dct.deliveryCentre) like UPPER(:term) ORDER BY dmt.modifiedDatetime DESC")
	List<DeliveryMasterT> searchByCentreTermAndIdsAndStages(@Param("term") String queryTerm,
			@Param("ids") List<?> ids, @Param("stages") List<Integer> stages);
	
	/*------------------End of delivery smart search queries-------------------*/
	
	
	List<DeliveryMasterT> findByOpportunityId(String opportunityId);

	@Query(value="select * from delivery_master_t where actual_start_date <= current_date and delivery_stage = 4",nativeQuery = true)
	List<DeliveryMasterT> findEngagementsPastActualStartDate();

	/*-------------------- Delivery dash board related queries ---------------------*/
	// delivery dash board for delivery center head, delivery cluster head, strategic initiative
	@Query(value="select DSMT.description, count(*) from delivery_master_t DMT INNER JOIN delivery_stage_mapping_t DSMT ON DMT.delivery_stage = DSMT.stage"
			+ " where DMT.delivery_centre_id in (:deliveryCentreIds) and DMT.delivery_stage IN (:deliveryStages) GROUP BY DSMT.stage", nativeQuery = true)
	List<Object[]> findEngagementByDeliveryStage(@Param ("deliveryCentreIds") List<Integer> deliveryCentreIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);
	
	@Query(value="select GMT.display_geography, count(*) from delivery_master_t DMT join opportunity_t OPP on "
			+ "DMT.opportunity_id = OPP.opportunity_id join customer_master_t CMT on "
			+ "CMT.customer_id = OPP.customer_id join geography_mapping_t GMT on "
			+ "CMT.geography = GMT.geography where DMT.delivery_centre_id in (:deliveryCentreIds) "
			+ "and DMT.delivery_stage IN (:deliveryStages) and GMT.active = 'true' "
			+ "GROUP BY GMT.display_geography", nativeQuery=true)
	List<Object[]> findEngagementByGeography(@Param ("deliveryCentreIds") List<Integer> deliveryCentreIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);
	
	@Query(value="select SSMT.display_sub_sp, count(*) from delivery_master_t DMT JOIN opportunity_t OPP ON "
			+ "OPP.opportunity_id = DMT.opportunity_id JOIN opportunity_sub_sp_link_t OSLT ON "
			+ "OSLT.opportunity_id = OPP.opportunity_id join sub_sp_mapping_t SSMT on "
			+ "OSLT.sub_sp = SSMT.sub_sp where DMT.delivery_centre_id in (:deliveryCentreIds) and "
			+ "DMT.delivery_stage IN (:deliveryStages) and OSLT.subsp_primary = 'true' and "
			+ "SSMT.active = 'true' GROUP BY SSMT.display_sub_sp", nativeQuery=true)
	List<Object[]> findEngagementBySubsp(@Param ("deliveryCentreIds") List<Integer> deliveryCentreIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);

	// delivery dashboard for Delivery managers
	@Query(value="select GMT.display_geography, count(*) from delivery_master_t DMT join opportunity_t OPP on "
			+ "OPP.opportunity_id = DMT.opportunity_id JOIN customer_master_t CMT ON "
			+ "CMT.customer_id = OPP.customer_id join geography_mapping_t GMT on "
			+ "CMT.geography = GMT.geography where DMT.delivery_master_id in (:deliveryMasterIds) and "
			+ "DMT.delivery_stage IN (:deliveryStages) and GMT.active = 'true' GROUP BY GMT.display_geography", nativeQuery=true)
	List<Object[]> findEngagementByGeographyForDM(@Param ("deliveryMasterIds") List<String> deliveryMasterIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);
	
	@Query(value="select SSMT.display_sub_sp, count(*) from delivery_master_t DMT JOIN opportunity_t OPP ON "
			+ "OPP.opportunity_id = DMT.opportunity_id JOIN opportunity_sub_sp_link_t OSLT ON "
			+ "OSLT.opportunity_id = OPP.opportunity_id join sub_sp_mapping_t SSMT on "
			+ "OSLT.sub_sp = SSMT.sub_sp where DMT.delivery_master_id in (:deliveryMasterIds) and "
			+ "DMT.delivery_stage IN (:deliveryStages) and OSLT.subsp_primary = 'true' and "
			+ "SSMT.active = 'true' GROUP BY SSMT.display_sub_sp", nativeQuery = true)
	List<Object[]> findEngagementBySubspForDM(@Param ("deliveryMasterIds") List<String> deliveryMasterIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);

	@Query(value="select DSMT.description, count(*) from delivery_master_t DMT INNER JOIN delivery_stage_mapping_t DSMT ON DMT.delivery_stage = DSMT.stage"
			+ " where DMT.delivery_master_id in (:deliveryMasterIds) and DMT.delivery_stage IN (:deliveryStages) GROUP BY DSMT.stage", nativeQuery = true)
	List<Object[]> findEngagementByDeliveryStageForDM(
			@Param ("deliveryMasterIds") List<String> deliveryMasterIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);
	
	//Delivery Report
	
	@Query(value = "select * from delivery_master_t where (delivery_master_id in "
			+ "(select delivery_master_id from delivery_master_manager_link_t where delivery_manager_id = (:userId)) "
			+ "AND (opportunity_id in (select distinct OPP.opportunity_id from opportunity_t OPP "
			+ "Join opportunity_sub_sp_link_t SSL on opp.opportunity_id = ssl.opportunity_id "
			+ "join sub_sp_mapping_t SSMT on SSL.sub_sp = SSMT.sub_sp Join customer_master_t CMT on "
			+ "OPP.customer_id = CMT.customer_id JOIN geography_mapping_t GMT on CMT.geography=GMT.geography "
			+ "Join iou_customer_mapping_t ICM on CMT.iou = ICM.iou "
			+ "where (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)) AND "
			+ "(OPP.country IN (:country) OR ('') in (:country)) "
			+ "AND (GMT.display_geography IN (:geography) OR ('') in (:geography))  AND "
			+ "(ICM.display_iou IN (:iouList) OR ('') in (:iouList)))) AND (delivery_stage in "
			+ "(:deliveryStage) OR (-1) in (:deliveryStage)) AND (delivery_centre_id in (:centreId) OR "
			+ "(-2) in (:centreId))) order by modified_datetime ", nativeQuery = true)
	List<DeliveryMasterT> getDeliveryEngagementByManager(
			@Param("userId") String userId,
			@Param("iouList") List<String> iouList,
			@Param("country") List<String> country,
			@Param("serviceLines") List<String> serviceLines,
			@Param("deliveryStage") List<Integer> deliveryStage,
			@Param("centreId") List<Integer> centreId,
			@Param("geography") List<String> displayGeography);

	@Query(value = "select * from delivery_master_t where (delivery_centre_id in (select delivery_centre_id from "
			+ "delivery_centre_t where delivery_cluster_id in "
			+ "(select delivery_cluster_id from delivery_cluster_t where delivery_cluster_head =(:userId))) AND "
			+ "(opportunity_id in (select distinct OPP.opportunity_id from opportunity_t OPP "
			+ "Join opportunity_sub_sp_link_t SSL on OPP.opportunity_id = SSL.opportunity_id join "
			+ "sub_sp_mapping_t SSMT on SSL.sub_sp = SSMT.sub_sp Join customer_master_t CMT on "
			+ "OPP.customer_id = CMT.customer_id JOIN geography_mapping_t GMT on CMT.geography=GMT.geography "
			+ "join iou_customer_mapping_t ICM on CMT.iou = ICM.iou where"
			+ "(SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)) "
			+ "AND (GMT.display_geography IN (:geography) OR ('') in (:geography)) AND"
			+ "(OPP.country IN (:country) OR ('') in (:country)) "
			+ "AND (ICM.display_iou IN (:iouList) OR ('') in (:iouList)))) "
			+ "AND (delivery_stage in (:deliveryStage) OR (-1) in (:deliveryStage)) "
			+ "AND (delivery_centre_id in (:centreId) OR (-2) in "
			+ "(:centreId))) order by modified_datetime ", nativeQuery = true)
	List<DeliveryMasterT> getDeliveryEngagementByCluster(
			@Param("userId") String userId,
			@Param("iouList") List<String> iouList,
			@Param("country") List<String> country,
			@Param("serviceLines") List<String> serviceLines,
			@Param("deliveryStage") List<Integer> deliveryStage,
			@Param("centreId") List<Integer> centreId,
			@Param("geography") List<String> displayGeography);

	@Query(value = "select * from delivery_master_t where (delivery_centre_id in (select delivery_centre_id from "
			+ "delivery_centre_t where delivery_centre_head = (:userId)) "
			+ " AND (opportunity_id in (select distinct OPP.opportunity_id from opportunity_t OPP "
			+ "Join opportunity_sub_sp_link_t SSL on opp.opportunity_id = SSL.opportunity_id "
			+ "join sub_sp_mapping_t SSMT on SSL.sub_sp = SSMT.sub_sp Join customer_master_t CMT on "
			+ "OPP.customer_id = CMT.customer_id JOIN geography_mapping_t GMT on CMT.geography=GMT.geography "
			+ "join iou_customer_mapping_t ICM on CMT.iou = ICM.iou "
			+ "where (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)) AND "
			+ "(OPP.country IN (:country) OR ('') in (:country)) "
			+ "AND (GMT.display_geography IN (:geography) OR ('') in (:geography)) AND "
			+ "(ICM.display_iou IN (:iouList) OR ('') in (:iouList)))) AND "
			+ "(delivery_stage in (:deliveryStage) OR (-1) in (:deliveryStage)) AND "
			+ "(delivery_centre_id in (:centreId) OR (-2) in (:centreId))) order by modified_datetime ", nativeQuery = true)
	List<DeliveryMasterT> getDeliveryEngagementByCentre(
			@Param("userId") String userId,
			@Param("iouList") List<String> iouList,
			@Param("country") List<String> country,
			@Param("serviceLines") List<String> serviceLines,
			@Param("deliveryStage") List<Integer> deliveryStage,
			@Param("centreId") List<Integer> centreId,
			@Param("geography") List<String> displayGeography);

	@Query(value = "select * from delivery_master_t where (opportunity_id in "
			+ "(select distinct OPP.opportunity_id from opportunity_t OPP Join "
			+ "opportunity_sub_sp_link_t SSL on opp.opportunity_id = SSL.opportunity_id "
			+ "Join sub_sp_mapping_t SSMT on SSL.sub_sp = SSMT.sub_sp Join customer_master_t CMT on "
			+ "OPP.customer_id = CMT.customer_id JOIN geography_mapping_t GMT on CMT.geography=GMT.geography "
			+ "join iou_customer_mapping_t ICM on CMT.iou = ICM.iou "
			+ "where (SSMT.display_sub_sp IN (:serviceLines) OR ('') in (:serviceLines)) AND "
			+ "(OPP.country IN (:country) OR ('') in (:country)) AND "
			+ "(GMT.display_geography IN (:geography) OR ('') in (:geography)) AND "
			+ "(ICM.display_iou IN (:iouList) OR ('') in (:iouList)))) AND "
			+ "(delivery_stage in (:deliveryStage) OR (-1) in (:deliveryStage)) AND"
			+ "(delivery_centre_id in (:centreId) OR (-2) in (:centreId)) order by modified_datetime ", nativeQuery = true)
	List<DeliveryMasterT> getDeliveryEngagementBySI(
			@Param("iouList") List<String> iouList,
			@Param("country") List<String> country,
			@Param("serviceLines") List<String> serviceLines,
			@Param("deliveryStage") List<Integer> deliveryStage,
			@Param("centreId") List<Integer> centreId,
			@Param("geography") List<String> displayGeography);

}
