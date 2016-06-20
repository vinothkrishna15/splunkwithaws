package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditConnectOpportunityLinkIdT;

@Repository
public interface AuditConnectOpportunityLinkIdTRepository extends CrudRepository<AuditConnectOpportunityLinkIdT, Long> {

}
