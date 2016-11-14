package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryIntimatedT;

@Repository
public interface DeliveryIntimetedRepository extends CrudRepository<DeliveryIntimatedT, String> {

}
