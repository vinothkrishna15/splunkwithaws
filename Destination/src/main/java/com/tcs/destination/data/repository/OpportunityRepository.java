package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityT;

@Repository
public interface OpportunityRepository extends
		CrudRepository<OpportunityT, String> {
//	List<OpportunityT> findByCustomerId(String custId);

//	List<OpportunityT> findByPartnerId(String partnerId);

}