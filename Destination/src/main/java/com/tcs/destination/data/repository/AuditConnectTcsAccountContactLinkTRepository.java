package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditConnectTcsAccountContactLinkT;

@Repository
public interface AuditConnectTcsAccountContactLinkTRepository extends CrudRepository<AuditConnectTcsAccountContactLinkT, Long>{

}
