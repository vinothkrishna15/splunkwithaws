package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryIntimatedCentreLinkT;


@Repository
public interface DeliveryIntimatedCentreLinkRepository extends CrudRepository<DeliveryIntimatedCentreLinkT, String>
 {
	@Query(value = "select distinct(delivery_intimated_id) from delivery_intimated_centre_link_t where delivery_centre_id in (:deliveryCentreIds)", nativeQuery = true)
	List<String> getDeliveryIntimatedIdsByCentreIds(
			@Param("deliveryCentreIds") List<Integer> deliveryIntimatedId);

	@Query(value = "select delivery_intimated_centre_link_id from delivery_intimated_centre_link_t where delivery_intimated_id = (:deliveryIntimatedId)", nativeQuery = true)
	List<String> getIdByDeliveryIntimatedId(
			@Param("deliveryIntimatedId") String deliveryIntimatedId);
}