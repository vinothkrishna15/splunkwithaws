package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditDeliveryIntimatedCentreLinkT;

@Repository
public interface AuditDeliveryIntimatedCentreLinkRepository extends CrudRepository<AuditDeliveryIntimatedCentreLinkT, Integer>{

}
