package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryStageMappingT;

@Repository
public interface DeliveryStageMappingRepository extends CrudRepository<DeliveryStageMappingT, Integer> {

	@Query(value="select description from delivery_stage_mapping_t where stage in (:deliveryStage)",nativeQuery=true)
	List<String> findDeliveryStageDescriptionByDeliveryStageId(@Param("deliveryStage")
			List<Integer> deliveryStage);
	
}
