package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.GroupCustomerT;

@Repository
public interface GroupCustomerRepository extends CrudRepository<GroupCustomerT, String>{

	Page<GroupCustomerT> findByGroupCustomerNameIsIn(
			List<String> grpCustomerNames, Pageable pageable);
	
	@Query(value = "SELECT gct.logo FROM GroupCustomerT gct WHERE gct.groupCustomerName=:id")
	byte[] getLogo(@Param("id") String id);

}
