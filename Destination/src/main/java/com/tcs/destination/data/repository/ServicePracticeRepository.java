package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ServicePracticeT;

@Repository
public interface ServicePracticeRepository extends CrudRepository<ServicePracticeT, Integer>{

	ServicePracticeT findBySp(String sp);

}
