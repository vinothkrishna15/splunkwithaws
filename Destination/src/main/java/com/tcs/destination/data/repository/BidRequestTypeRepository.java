package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BidRequestTypeMappingT;

@Repository
public interface BidRequestTypeRepository extends CrudRepository<BidRequestTypeMappingT, String>{

}
