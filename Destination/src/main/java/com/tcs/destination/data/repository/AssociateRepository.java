package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AssociateT;

@Repository
public interface AssociateRepository extends CrudRepository<AssociateT, String> {

}
