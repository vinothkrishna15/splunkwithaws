package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditConnectOfferingLinkT;

@Repository
public interface AuditConnectOfferingLinkTRepository extends CrudRepository<AuditConnectOfferingLinkT, Long> {

}
