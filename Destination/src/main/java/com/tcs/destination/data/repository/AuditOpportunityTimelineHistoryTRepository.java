package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunityTimelineHistoryT;

@Repository
public interface AuditOpportunityTimelineHistoryTRepository extends CrudRepository<AuditOpportunityTimelineHistoryT, Long> {

}
