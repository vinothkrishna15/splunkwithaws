package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryPmoT;

@Repository
public interface DeliveryPmoRepository  extends
CrudRepository<DeliveryPmoT, String>{

	@Query(value = "select case when DC.deliveryCentreHead is NULL then DCL.deliveryClusterHead else DC.deliveryCentreHead end"
			+ " from DeliveryPmoT DP"
			+ " join DP.deliveryCentreT DC join DC.deliveryClusterT DCL where DP.pmoId = (:pmoId)")
	List<String> getDeliveryCentreHeadsByPmo(@Param("pmoId") String userId);

}
