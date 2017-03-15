package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.QuarterlyHealthcardPercentage;

@Repository
public interface QuarterlyHealthcardPercentageRepository extends CrudRepository<QuarterlyHealthcardPercentage, Integer> {

}
