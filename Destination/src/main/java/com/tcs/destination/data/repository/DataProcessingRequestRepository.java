package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DataProcessingRequestT;

/**
 * @author
 *
 */
@Repository
public interface DataProcessingRequestRepository extends PagingAndSortingRepository<DataProcessingRequestT, Long> {
	
	List<DataProcessingRequestT> findByRequestTypeAndStatus(int requestType, int status);
	
	Page<DataProcessingRequestT> findByStatus(int status, Pageable pageable);
	
	@Query("select data from DataProcessingRequestT data")
	Page<DataProcessingRequestT> getAll(Pageable pageable);

}
