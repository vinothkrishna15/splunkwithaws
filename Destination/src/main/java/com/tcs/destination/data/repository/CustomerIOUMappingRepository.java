package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.IouCustomerMappingT;

@Repository
public interface CustomerIOUMappingRepository extends CrudRepository<IouCustomerMappingT,String>{
	
	@Query(value ="select distinct display_iou from iou_customer_mapping_t",nativeQuery = true)
	List<Object> findDistintDisplayIou();

	List<IouCustomerMappingT> findByActiveTrue();
}
