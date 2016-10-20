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

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) AND delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t where delivery_cluster_id in ("
			+ " SELECT delivery_cluster_id FROM delivery_cluster_t WHERE delivery_cluster_head=(:userId))  OR delivery_centre_id=-1) AND "
			+ " UPPER(opportunity_id) like UPPER(:term) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END",nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryClusterDetailsById(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId, @Param("stage") int stage);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) AND delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t WHERE delivery_centre_head=(:userId)) AND "
			+ " UPPER(opportunity_id) like UPPER(:term) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END",nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryCentreDetailsById(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId, @Param("stage") int stage);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) AND delivery_master_id in (SELECT delivery_master_id FROM delivery_master_manager_link_t WHERE delivery_manager_id=(:userId))"
			+ " AND UPPER(opportunity_id) like UPPER(:term) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END",nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryManagerDetailsById(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId, @Param("stage") int stage);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) AND delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t where delivery_cluster_id in ("
			+ " SELECT delivery_cluster_id FROM delivery_cluster_t WHERE delivery_cluster_head=(:userId) OR delivery_centre_id=-1) AND UPPER(delivery_centre) like UPPER(:term)) "
			+ " ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryClusterDetailsByDeliveryCentres(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId, @Param("stage") int stage);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) AND delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t WHERE delivery_centre_head=(:userId) "
			+ "  AND UPPER(delivery_centre) like UPPER(:term)) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryCentreDetailsByDeliveryCentres(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId, @Param("stage") int stage);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) AND delivery_master_id in (SELECT delivery_master_id FROM delivery_master_manager_link_t"
			+ " WHERE delivery_manager_id = (:userId)) AND delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t "
			+ " WHERE UPPER(delivery_centre) like UPPER(:term)) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryManagerDetailsByDeliveryCentres(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId, @Param("stage") int stage);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) AND delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t WHERE delivery_cluster_id in ("
			+ " SELECT delivery_cluster_id FROM delivery_cluster_t WHERE delivery_cluster_head=(:userId)) OR delivery_centre_id=-1) AND opportunity_id IN (SELECT opportunity_id FROM opportunity_t WHERE customer_id IN ("
			+ " SELECT customer_id from customer_master_t WHERE UPPER(customer_name) like UPPER(:term))) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END ", nativeQuery=true) 
	List<DeliveryMasterT> searchDeliveryClusterDetailsByCustomerName(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId, @Param("stage") int stage);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) AND delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t WHERE delivery_centre_head=(:userId)) "
			+ " AND opportunity_id IN (SELECT opportunity_id FROM opportunity_t WHERE customer_id IN (SELECT customer_id from customer_master_t WHERE UPPER(customer_name) like UPPER(:term))) "
			+ " ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryCentreDetailsByCustomerName(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId, @Param("stage") int stage);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) AND delivery_master_id in (SELECT delivery_master_id FROM delivery_master_manager_link_t WHERE delivery_manager_id=(:userId))"
			+ " AND opportunity_id IN (SELECT opportunity_id FROM opportunity_t WHERE customer_id IN (SELECT customer_id from customer_master_t WHERE UPPER(customer_name) like UPPER(:term))) "
			+ " ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryManagerDetailsByCustomerName(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId, @Param("stage") int stage);



	/*------------------End of delivery smart search queries-------------------*/

	List<DeliveryMasterT> findByOpportunityId(String opportunityId);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) "
			+ " AND UPPER(opportunity_id) like UPPER(:term) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchForSIDetailsById(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("stage") int stage);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) "
			+ " AND opportunity_id IN (SELECT opportunity_id FROM opportunity_t WHERE customer_id IN ( SELECT customer_id from customer_master_t WHERE UPPER(customer_name) like UPPER(:term))) "
			+ " ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END ", nativeQuery=true) 
	List<DeliveryMasterT> searchForSIDetailsByCustomerName(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("stage") int stage);

	@Query(value="SELECT * FROM delivery_master_t where (delivery_stage = (:stage) OR (-1) =(:stage)) "
			+ " AND delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t "
			+ " where UPPER(delivery_centre) like UPPER(:term)) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true) 
	List<DeliveryMasterT> searchForSIDetailsByCentres(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("stage") int stage);

	@Query(value="select * from delivery_master_t where actual_start_date < current_date and delivery_stage = 4",nativeQuery = true)
	List<DeliveryMasterT> findEngagementsPastActualStartDate();

	/*-------------------- Delivery dash board related queries ---------------------*/
	// delivery dash board for delivery center head, delivery cluster head, strategic initiative
	@Query(value="select DSMT.description, count(*) from delivery_master_t DMT INNER JOIN delivery_stage_mapping_t DSMT ON DMT.delivery_stage = DSMT.stage"
			+ " where DMT.delivery_centre_id in (:deliveryCentreIds) and DMT.delivery_stage IN (:deliveryStages) GROUP BY DSMT.stage", nativeQuery = true)
	List<Object[]> findEngagementByDeliveryStage(@Param ("deliveryCentreIds") List<Integer> deliveryCentreIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);

	@Query(value="select  CMT.geography, count(*) from opportunity_t OPP JOIN delivery_master_t DMT ON (OPP.opportunity_id = DMT.opportunity_id)"
			+ " JOIN customer_master_t CMT ON CMT.customer_id = OPP.customer_id where DMT.delivery_centre_id in (:deliveryCentreIds) and DMT.delivery_stage "
			+ " IN (:deliveryStages) GROUP BY CMT.geography", nativeQuery=true)
	List<Object[]> findEngagementByGeography(@Param ("deliveryCentreIds") List<Integer> deliveryCentreIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);

	@Query(value="select  OSLT.sub_sp, count(*) from opportunity_t OPP JOIN delivery_master_t DMT ON (OPP.opportunity_id = DMT.opportunity_id) "
			+ " JOIN opportunity_sub_sp_link_t OSLT ON OSLT.opportunity_id = OPP.opportunity_id where DMT.delivery_centre_id in (:deliveryCentreIds) and"
			+ " DMT.delivery_stage IN (:deliveryStages) and OSLT.subsp_primary = 'true' GROUP BY OSLT.sub_sp", nativeQuery = true)
	List<Object[]> findEngagementBySubsp(@Param ("deliveryCentreIds") List<Integer> deliveryCentreIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);

	// delivery dashboard for Delivery managers
	@Query(value="select  CMT.geography, count(*) from opportunity_t OPP JOIN delivery_master_t DMT ON (OPP.opportunity_id = DMT.opportunity_id)"
			+ " JOIN customer_master_t CMT ON CMT.customer_id = OPP.customer_id where DMT.delivery_master_id in (:deliveryMasterIds) and DMT.delivery_stage "
			+ " IN (:deliveryStages) GROUP BY CMT.geography", nativeQuery=true)
	List<Object[]> findEngagementByGeographyForDM(@Param ("deliveryMasterIds") List<String> deliveryMasterIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);

	@Query(value="select  OSLT.sub_sp, count(*) from opportunity_t OPP JOIN delivery_master_t DMT ON (OPP.opportunity_id = DMT.opportunity_id) "
			+ " JOIN opportunity_sub_sp_link_t OSLT ON OSLT.opportunity_id = OPP.opportunity_id where DMT.delivery_master_id in (:deliveryMasterIds) and"
			+ " DMT.delivery_stage IN (:deliveryStages) and OSLT.subsp_primary = 'true' GROUP BY OSLT.sub_sp", nativeQuery = true)
	List<Object[]> findEngagementBySubspForDM(@Param ("deliveryMasterIds") List<String> deliveryMasterIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);

	@Query(value="select DSMT.description, count(*) from delivery_master_t DMT INNER JOIN delivery_stage_mapping_t DSMT ON DMT.delivery_stage = DSMT.stage"
			+ " where DMT.delivery_master_id in (:deliveryMasterIds) and DMT.delivery_stage IN (:deliveryStages) GROUP BY DSMT.stage", nativeQuery = true)
	List<Object[]> findEngagementByDeliveryStageForDM(
			@Param ("deliveryMasterIds") List<String> deliveryMasterIds,
			@Param ("deliveryStages") List<Integer> deliveryStages);
}
