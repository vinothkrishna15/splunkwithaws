package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunitySalesSupportLinkT;

@Repository
public interface AuditOpportunitySalesSupportLinkTRepository extends CrudRepository<AuditOpportunitySalesSupportLinkT, Long> {

	List<AuditOpportunitySalesSupportLinkT> findByOldOpportunityIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(
			String entityId);

	List<AuditOpportunitySalesSupportLinkT> findByOldOpportunityId(String oppId);

}
