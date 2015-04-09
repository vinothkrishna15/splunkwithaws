package com.tcs.destination.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.UserT;

@Repository
public interface OpportunityRepository extends
		CrudRepository<OpportunityT, String> {

	OpportunityT findByOpportunityNameIgnoreCaseLike(String opportunityname);
	
	List<OpportunityT> findByCustomerIdAndOpportunityRequestReceiveDateAfter(String customerId,Date fromDate);
	
	List<OpportunityT> findByOpportunityOwner(String primaryOwner);

	OpportunityT findByOpportunityId(String opportunityId);
	
	@Query(value = "select * from opportunity_t where opportunity_id in"
			+ " (select distinct opportunity_id from collaboration_comment_t where opportunity_id in"
			+ " (select opportunity_id from collaboration_comment_t "
			+ "where opportunity_id!='' and "
			+ "user_id!=?1 "
			+ "order by updated_datetime Desc))", nativeQuery = true)
    List<OpportunityT> findTrendingOpportunities(String userId);
	
}