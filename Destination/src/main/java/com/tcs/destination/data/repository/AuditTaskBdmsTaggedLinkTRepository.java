package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditTaskBdmsTaggedLinkT;

@Repository
public interface AuditTaskBdmsTaggedLinkTRepository extends CrudRepository<AuditTaskBdmsTaggedLinkT, Long>{

	List<AuditTaskBdmsTaggedLinkT> findByOldTaskIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(
			String entityId);

}
