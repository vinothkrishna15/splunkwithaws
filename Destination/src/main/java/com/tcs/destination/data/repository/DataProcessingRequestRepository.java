package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DataProcessingRequestT;

/**
 * @author
 *
 */
@Repository
public interface DataProcessingRequestRepository extends CrudRepository<DataProcessingRequestT, Long> {
	
	public List<DataProcessingRequestT> findByRequestTypeAndStatus(int requestType, int status);

}
