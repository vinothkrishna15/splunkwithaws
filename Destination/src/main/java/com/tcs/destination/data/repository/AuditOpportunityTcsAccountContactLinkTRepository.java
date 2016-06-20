package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunityTcsAccountContactLinkT;

@Repository
public interface AuditOpportunityTcsAccountContactLinkTRepository extends CrudRepository<AuditOpportunityTcsAccountContactLinkT, Long>{

	List<AuditOpportunityTcsAccountContactLinkT> findByOldOpportunityId(String oppId);

}
