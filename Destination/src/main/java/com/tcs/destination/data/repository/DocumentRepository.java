package com.tcs.destination.data.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.DocumentRepositoryT;

@Repository
public interface DocumentRepository extends CrudRepository<DocumentRepositoryT, String> {

	DocumentRepositoryT findByDocumentId(String documentId);

}

