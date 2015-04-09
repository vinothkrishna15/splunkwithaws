package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BidDetailsT;

@Repository
public interface BidDetailsTRepository extends
		CrudRepository<BidDetailsT, String> {

}