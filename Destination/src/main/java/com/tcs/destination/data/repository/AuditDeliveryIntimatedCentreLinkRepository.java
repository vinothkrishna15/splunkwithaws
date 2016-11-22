package com.tcs.destination.data.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditDeliveryIntimatedCentreLinkT;
import com.tcs.destination.bean.DeliveryIntimatedT;

@Repository
public interface AuditDeliveryIntimatedCentreLinkRepository extends CrudRepository<AuditDeliveryIntimatedCentreLinkT, Integer>{

	List<AuditDeliveryIntimatedCentreLinkT> findByDeliveryIntimatedId(String intiEngId);

	@Query(value="select DC.delivery_centre from audit_delivery_intimated_centre_link_t ADC join "
			+ "delivery_centre_t DC on ADC.delivery_centre_id = DC.delivery_centre_id where "
			+ "ADC.created_modified_datetime = (:modifiedDatetime) and ADC.operation_type = (:operationType)",nativeQuery=true)
	List<String> getRejectedDeliveryCentreNames(@Param("modifiedDatetime") Timestamp modifiedDatetime,
			@Param("operationType") Integer operationType);
	
}
