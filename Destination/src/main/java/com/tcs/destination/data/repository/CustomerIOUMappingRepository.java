package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.IouCustomerMappingT;

public interface CustomerIOUMappingRepository extends CrudRepository<IouCustomerMappingT,String>{
	
	@Query(value ="select distinct display_iou from iou_customer_mapping_t",nativeQuery = true)
	List<Object> findDistintDisplayIou();
}
