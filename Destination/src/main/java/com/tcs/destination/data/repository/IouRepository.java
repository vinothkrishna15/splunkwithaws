package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.IouCustomerMappingT;

public interface IouRepository extends CrudRepository<IouCustomerMappingT, String> {

    List<IouCustomerMappingT> findByDisplayIou(String displayIOU);
    
    @Query(value="select distinct display_iou from iou_customer_mapping_t",nativeQuery=true)
    List<String> findDistinctDisplayIou();

	IouCustomerMappingT findByActiveTrueAndIou(String iou);

}
