package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunitySubSpLinkT;

@Repository
public interface OpportunitySubSpLinkTRepository extends
		CrudRepository<OpportunitySubSpLinkT, String> {

}