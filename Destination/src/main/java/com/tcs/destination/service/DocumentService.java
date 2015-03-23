package com.tcs.destination.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.bean.UserT;
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
	
	public byte[] download(String documentId) {
		DocumentRepositoryT docRep = documentRepository.findByDocumentId(documentId);
		return docRep.getFileReference();
	}
	
	public String saveDocument(String documentName,
			String documentSearchKeywords, String documentType,
			String entityType, String parentEntity, String parentEntityId,
			String commentId, String connectId, String customerId,
			String opportunityId, String partnerId, String taskId,
			String uploadedBy, MultipartFile file) throws IOException {
			DocumentRepositoryT document=new DocumentRepositoryT();
			document.setCommentId(commentId);
			document.setConnectId(connectId);
			document.setCustomerId(customerId);
			document.setDocumentName(documentName);
			document.setDocumentSearchKeywords(documentSearchKeywords);
			document.setDocumentType(documentType);
			document.setEntityType(entityType);
			document.setFileReference(file.getBytes());
			document.setOpportunityId(opportunityId);
			document.setParentEntity(parentEntity);
			document.setParentEntityId(parentEntityId);
			document.setPartnerId(partnerId);
			document.setTaskId(taskId);
			UserT user=new UserT();
			user.setUserId(uploadedBy);
			document.setUserT(user);;
			document.setUploadedDatetime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			return documentRepository.save(document).getDocumentId();
	}

}
