package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditDeliveryIntimatedT;

@Repository
public interface AuditDeliveryIntimatedRepository extends CrudRepository<AuditDeliveryIntimatedT, Integer> {

}
