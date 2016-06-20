package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditOpportunityT;

@Repository
public interface AuditOpportunityRepository extends
CrudRepository<AuditOpportunityT,Long>{

	List<AuditOpportunityT> findByOperationTypeAndOpportunityIdAndNotifiedOrderByNewModifiedDatetimeDesc(
			int operationType, String entityId, boolean notified);

	List<AuditOpportunityT> findByOpportunityIdAndNotifiedFalseOrderByNewModifiedDatetimeDesc(
			String entityId);

	AuditOpportunityT findFirstByOpportunityIdAndNotifiedFalseOrderByNewModifiedDatetimeDesc(
			String entityId);

	List<AuditOpportunityT> findByOpportunityId(String oppId);

	@Query(value = "select* from audit_opportunity_t where opportunity_id  =:oppId and (operation_type=1 or old_sales_stage_code <> new_sales_stage_code) order by new_modified_datetime asc", nativeQuery = true)
	List<AuditOpportunityT> getSalesCodeChanges(@Param("oppId") String oppId);

}
