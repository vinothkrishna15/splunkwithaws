package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityReopenRequestT;

@Repository
public interface OpportunityReopenRequestRepository extends
		CrudRepository<OpportunityReopenRequestT, String> {

}
