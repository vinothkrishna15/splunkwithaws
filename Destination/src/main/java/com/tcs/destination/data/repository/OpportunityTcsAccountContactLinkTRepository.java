package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;

@Repository
public interface OpportunityTcsAccountContactLinkTRepository extends
		CrudRepository<OpportunityTcsAccountContactLinkT, String> {
	
}
