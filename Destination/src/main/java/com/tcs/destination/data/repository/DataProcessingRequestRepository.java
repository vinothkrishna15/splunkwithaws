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
	
	Page<DataProcessingRequestT> findByStatusAndRequestTypeBetween(int status, int start, int end, Pageable pageable);
	
	Page<DataProcessingRequestT> findByStatusAndRequestTypeIn(int status, List<Integer> requestTypes, Pageable pageable);
	
	@Query("select data from DataProcessingRequestT data")
	Page<DataProcessingRequestT> getAll(Pageable pageable);

}
