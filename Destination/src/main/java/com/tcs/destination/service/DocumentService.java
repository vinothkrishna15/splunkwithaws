package com.tcs.destination.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.data.repository.DocumentRepository;

public class DocumentService {

	@Autowired 
	DocumentRepository documentRepository;

//	public int save(DocumentRepositoryT docrep) 
//	{
//		DocumentRepositoryT documentrep=documentRepository.save(docrep);
//		if(documentrep != null)
//		    return 1;
//		else
//			return 0;
//	}

	public DocumentRepositoryT findByDocumentId(String documentId) {
		DocumentRepositoryT docRep = documentRepository.findByDocumentId(documentId);
		return docRep;
	}

}
