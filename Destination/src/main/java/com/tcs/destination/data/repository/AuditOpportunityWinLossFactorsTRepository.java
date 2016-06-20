package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunityWinLossFactorsT;

@Repository
public interface AuditOpportunityWinLossFactorsTRepository extends CrudRepository<AuditOpportunityWinLossFactorsT, Long>{

	List<AuditOpportunityWinLossFactorsT> findByOldOpportunityId(String oppId);

}
