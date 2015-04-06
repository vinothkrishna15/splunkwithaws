package com.tcs.destination.data.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tcs.destination.bean.DocumentRepositoryT;

@Repository
public interface DocumentRepository extends CrudRepository<DocumentRepositoryT, String> {

	DocumentRepositoryT findByDocumentId(String documentId);
	
	@Query(value="select d from DocumentRepositoryT d "
			+ "where (documentName=(:documentName) OR (:documentName)='') and "
			+ "(customerId=(:customerId) OR (:customerId)='') and "
			+ "(partnerId=(:partnerId) OR (:partnerId)='') and "
			+ "(connectId=(:connectId) OR (:connectId)='') and "
			+ "(opportunityId=(:opportunityId) OR (:opportunityId)='') "
			+ "and (taskId=(:taskId) OR (:taskId)='')")
	DocumentRepositoryT findDocument(@Param("documentName") String documentName,
			@Param("customerId") String customerId,
			@Param("partnerId") String partnerId,
			@Param("connectId") String connectId,
			@Param("opportunityId") String opportunityId,
			@Param("taskId") String taskId);

}

