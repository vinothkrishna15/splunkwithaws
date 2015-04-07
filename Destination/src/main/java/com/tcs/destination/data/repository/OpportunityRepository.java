package com.tcs.destination.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.UserT;

@Repository
public interface OpportunityRepository extends
		CrudRepository<OpportunityT, String> {

	OpportunityT findByOpportunityNameIgnoreCaseLike(String opportunityname);
	
	List<OpportunityT> findByCustomerIdAndOpportunityRequestReceiveDateAfter(String customerId,Date fromDate);
	
	List<OpportunityT> findByUserT(UserT userT);

	OpportunityT findByOpportunityId(String opportunityId);

}