package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditBidDetailsT;

@Repository
public interface AuditBidDetailsTRepository extends CrudRepository<AuditBidDetailsT, Long> {

	List<AuditBidDetailsT> findByOldOpportunityIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(
			String entityId);

	AuditBidDetailsT findFirstByOldOpportunityIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(
			String entityId);

	List<AuditBidDetailsT> findByOldOpportunityId(String oppId);

}
