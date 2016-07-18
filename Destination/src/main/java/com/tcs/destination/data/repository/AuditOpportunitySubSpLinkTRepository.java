package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunitySubSpLinkT;

@Repository
public interface AuditOpportunitySubSpLinkTRepository extends CrudRepository<AuditOpportunitySubSpLinkT, Long> {

	List<AuditOpportunitySubSpLinkT> findByOldOpportunityId(String oppId);

}
