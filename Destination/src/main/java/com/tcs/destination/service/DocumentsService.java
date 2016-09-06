package com.tcs.destination.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.DocumentsT;
import com.tcs.destination.data.repository.DocumentsTRepository;
import com.tcs.destination.exception.DestinationException;

/**
 * This service is used to save and delete documents
 * 
 * @author TCS
 *
 */
@Service
public class DocumentsService {

	private static final Logger logger = LoggerFactory
			.getLogger(DocumentsService.class);
	
	@Autowired
	DocumentsTRepository documentsTRepository;

	/**
	 * This method is used to find the document using id
	 * @param documentId
	 * @return
	 * @throws Exception
	 */
	public DocumentsT findByDocumentId(String documentId) throws Exception{
		logger.debug("Start: Inside  findByDocumentId() of DocumentService");
		DocumentsT docRep = documentsTRepository.findByDocumentsId(documentId);
		if(docRep==null){
			logger.error("NOT_FOUND: No Relevent Data/document Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data/document Found in the database");
		}
		logger.debug("End: Inside  findByDocumentId() of DocumentService");
		return docRep;
	}
}
