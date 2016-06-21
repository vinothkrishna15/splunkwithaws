package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditConnectSecondaryOwnerLinkT;

@Repository
public interface AuditConnectSecondaryOwnerLinkTRepository extends CrudRepository<AuditConnectSecondaryOwnerLinkT, Long>{

	/*List<AuditConnectSecondaryOwnerLinkT> findByOldConnectIdAndNotifiedFalseOrderByCreatedModifiedDatetime(
			String entityId);*/

}
