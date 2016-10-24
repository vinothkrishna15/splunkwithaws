package com.tcs.destination.data.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryRequirementT;

@Repository
public interface DeliveryRequirementRepository extends
		CrudRepository<DeliveryRequirementT, String> {

	List<DeliveryRequirementT> findByFulfillmentDateBetween(Date startDate, Date endDate);

	List<DeliveryRequirementT> findByRequirementStartDateBetween(Date startDate,
			Date endDate);

	@Query(value="select distinct DRQT.* from delivery_requirement_t DRQT "
			+ "join delivery_resources_t DRST on DRST.delivery_rgs_id = DRQT.delivery_rgs_id "
			+ "join delivery_master_t DMT on DMT.delivery_master_id = DRST.delivery_master_id "
			+ "join opportunity_sub_sp_link_t OSLT on OSLT.opportunity_id = DMT.opportunity_id "
			+ "join sub_sp_mapping_t SSMT on SSMT.sub_sp = OSLT.sub_sp "
			+ "where (SSMT.display_sub_sp = (:displaySubSp) OR (:displaySubSp) = '') "
			+ "and DRQT.fulfillment_date between (:weekStartDate) and (:weekEndDate)",
			nativeQuery=true)
	List<DeliveryRequirementT> getFulfilledRequirement(@Param("displaySubSp") String displaySubSp,
			@Param("weekStartDate") Date weekStartDate,@Param("weekEndDate") Date weekEndDate);
	
	@Query(value="select distinct DRQT.* from delivery_requirement_t DRQT "
			+ "join delivery_resources_t DRST on DRST.delivery_rgs_id = DRQT.delivery_rgs_id "
			+ "join delivery_master_t DMT on DMT.delivery_master_id = DRST.delivery_master_id "
			+ "join opportunity_sub_sp_link_t OSLT on OSLT.opportunity_id = DMT.opportunity_id "
			+ "join sub_sp_mapping_t SSMT on SSMT.sub_sp = OSLT.sub_sp "
			+ "where (SSMT.display_sub_sp = (:displaySubSp) OR (:displaySubSp) = '') "
			+ "and DRQT.requirement_start_date between (:weekStartDate) and (:weekEndDate)",
			nativeQuery=true)
	List<DeliveryRequirementT> getOpenRequirement(@Param("displaySubSp") String displaySubSp,
			@Param("weekStartDate") Date weekStartDate,@Param("weekEndDate") Date weekEndDate);
	
}
