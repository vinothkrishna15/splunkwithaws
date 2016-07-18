package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditTaskT;

@Repository
public interface AuditTaskTRepository extends CrudRepository<AuditTaskT, Long>{

	AuditTaskT findFirstByTaskIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(
			String entityId);

}
