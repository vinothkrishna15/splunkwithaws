package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityReopenRequestT;

@Repository
public interface OpportunityReopenRequestRepository extends
		CrudRepository<OpportunityReopenRequestT, String> {

	List<OpportunityReopenRequestT> findByRequestedBy(String userId);
	
	@Query(value="select * from opportunity_reopen_request_t where opportunity_id=?1 and approved_rejected_by is null",nativeQuery=true)
	List<OpportunityReopenRequestT> findByOpportunityId(String oppId);

}
