package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunityDeliveryCentreT;

@Repository
public interface AuditOpportunityDeliveryCenterRepository extends CrudRepository<AuditOpportunityDeliveryCentreT, Integer> {

	@Query(value="select AODCT.* from audit_opportunity_delivery_centre_t AODCT join opportunity_t OPP "
			+ "on AODCT.opportunity_id = OPP.opportunity_id where AODCT.opportunity_id = (:opportunityId) "
			+ "and AODCT.operation_type = 1 "
			+ "and AODCT.created_modified_datetime >= OPP.modified_datetime" , nativeQuery = true)
	List<AuditOpportunityDeliveryCentreT> findNewlyAddedDeliveryCentresForOpportunity(@Param("opportunityId")
			String opportunityId);

}
