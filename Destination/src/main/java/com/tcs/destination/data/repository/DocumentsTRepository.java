package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DocumentsT;

@Repository
public interface DocumentsTRepository extends CrudRepository<DocumentsT, String> {

	DocumentsT findByDocumentsId(String documentId);

}
