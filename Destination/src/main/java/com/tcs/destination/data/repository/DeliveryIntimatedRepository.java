package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryIntimatedT;


@Repository
public interface DeliveryIntimatedRepository extends CrudRepository<DeliveryIntimatedT, String> {
	
}
