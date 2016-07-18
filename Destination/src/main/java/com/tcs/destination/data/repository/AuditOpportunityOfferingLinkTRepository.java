package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunityOfferingLinkT;

@Repository
public interface AuditOpportunityOfferingLinkTRepository extends CrudRepository<AuditOpportunityOfferingLinkT, Long> {

	List<AuditOpportunityOfferingLinkT> findByOldOpportunityId(String oppId);

}
