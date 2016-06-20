package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditConnectT;

@Repository
public interface AuditConnectTRepository extends CrudRepository<AuditConnectT, Long> {

	AuditConnectT findFirstByConnectIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(
			String entityId);

}
