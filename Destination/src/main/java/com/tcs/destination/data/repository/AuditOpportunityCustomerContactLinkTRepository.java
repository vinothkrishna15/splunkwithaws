package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunityCustomerContactLinkT;

@Repository
public interface AuditOpportunityCustomerContactLinkTRepository extends CrudRepository<AuditOpportunityCustomerContactLinkT, Long>{

	List<AuditOpportunityCustomerContactLinkT> findByOldOpportunityId(String oppId);

}
