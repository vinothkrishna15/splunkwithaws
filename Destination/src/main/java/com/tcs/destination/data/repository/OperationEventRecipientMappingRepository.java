package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OperationEventRecipientMappingT;

@Repository
public interface OperationEventRecipientMappingRepository extends CrudRepository<OperationEventRecipientMappingT, Integer> {

	List<OperationEventRecipientMappingT> findByOperationType(Integer ordinal);

}
