package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditCustomerMasterT;

@Repository
public interface AuditCustomerMasterTRepository extends CrudRepository<AuditCustomerMasterT, Long> {

}
