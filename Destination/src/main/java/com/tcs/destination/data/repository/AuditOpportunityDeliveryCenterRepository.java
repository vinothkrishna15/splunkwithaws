package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunityDeliveryCentreT;

@Repository
public interface AuditOpportunityDeliveryCenterRepository extends CrudRepository<AuditOpportunityDeliveryCentreT, Integer> {

}
