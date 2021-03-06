package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunityPartnerLinkT;

@Repository
public interface AuditOpportunityPartnerLinkTRepository extends CrudRepository<AuditOpportunityPartnerLinkT, Long>{

	List<AuditOpportunityPartnerLinkT> findByOldOpportunityId(String oppId);

}
