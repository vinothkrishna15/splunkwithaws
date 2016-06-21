package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditBidOfficeGroupOwnerLinkT;

@Repository
public interface AuditBidOfficeGroupOwnerLinkTRepository extends CrudRepository<AuditBidOfficeGroupOwnerLinkT, Long>{

	List<AuditBidOfficeGroupOwnerLinkT> findByOldBidIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(
			String bidId);

	List<AuditBidOfficeGroupOwnerLinkT>  findByOldBidId(String bidId);

}
