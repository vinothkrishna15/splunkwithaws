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

	@Query(value = "select GC from GroupCustomerT GC"
			+ " where (GC.groupCustomerName in (:grpCustomerNames) or ('') in (:grpCustomerNames))"
			+ " and upper(GC.groupCustomerName) like upper((:nameWith))"
			+ " and GC.groupCustomerName NOT IN (:preferedCust)")
	Page<GroupCustomerT> getGrpCustomersByNameWith(@Param("grpCustomerNames")
			List<String> grpCustomerNames,@Param("nameWith") String nameWith, @Param("preferedCust")
			List<String> preferedCust, Pageable pageable);

}
