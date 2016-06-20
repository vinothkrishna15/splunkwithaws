package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunityCompetitorLinkT;

@Repository
public interface AuditOpportunityCompetitorLinkTRepository extends CrudRepository<AuditOpportunityCompetitorLinkT, Long> {

	List<AuditOpportunityCompetitorLinkT> findByOldOpportunityId(String oppId);

}
