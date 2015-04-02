package com.tcs.destination.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DocumentRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants.EntityType;

@Component
public class DocumentService {

	@Autowired 
	DocumentRepository documentRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
	
	@Value("${fileBaseDir}")
	private String fileBasePath;
	
	//@Autowired
    //Environment env;
	
	//private String fileBasePath1 = env.getProperty("fileBaseDir");
	
//	public int save(DocumentRepositoryT docrep) 
//	{
//		DocumentRepositoryT documentrep=documentRepository.save(docrep);
//		if(documentrep != null)
//		    return 1;
//		else
//			return 0;
//	}

	public DocumentRepositoryT findByDocumentId(String documentId) throws Exception{
		DocumentRepositoryT docRep = documentRepository.findByDocumentId(documentId);
		if(docRep==null){
			throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data/document Found in the database");
		}
		return docRep;
	}
	
	public String saveDocument(String documentName,
			 String documentType,
			String entityType, String parentEntity, String parentEntityId,
			String commentId, String connectId, String customerId,
			String opportunityId, String partnerId, String taskId,
			String uploadedBy, MultipartFile file) throws Exception {
		
			DocumentRepositoryT document=new DocumentRepositoryT();
			if(!commentId.equals(""))
			document.setCommentId(commentId);
			if(!connectId.equals(""))
			document.setConnectId(connectId);
			if(!customerId.equals(""))
			document.setCustomerId(customerId);
			
			document.setDocumentName(documentName);
			//document.setDocumentSearchKeywords(documentSearchKeywords);
			document.setDocumentType(documentType);
			document.setEntityType(entityType);
			document.setFileReference(fileBasePath);
			if(!opportunityId.equals(""))
			document.setOpportunityId(opportunityId);
			
			document.setParentEntity(parentEntity);
			document.setParentEntityId(parentEntityId);
			if(!partnerId.equals(""))
			document.setPartnerId(partnerId);
			if(!taskId.equals(""))
			document.setTaskId(taskId);
			UserT user=new UserT();
			user.setUserId(uploadedBy);
			document.setUserT(user);
			document.setUploadedBy(uploadedBy);
			document.setUploadedDatetime(new Timestamp(Calendar.getInstance().getTimeInMillis()));

			//document.setFileReference(file.getBytes());
			/**/
			
			String docId = "";
			validateInputs(document);
			logger.info("validated input(document record) for insertion");
			
			if(documentRepository.save(document)!=null){
				docId = document.getDocumentId();
				logger.info("document record saved with id: " + docId);
				String entityId = getEntityId(document);
				logger.info(docId + " - entity id: " + entityId);
				String saveDirLoc = getPathFromForm(entityType,entityId);
				logger.info(docId + " - Directory to be stored : " + saveDirLoc);
				if (!file.isEmpty()) {
					saveFile(file,saveDirLoc,docId);
					logger.info(docId + " - File saved at " + saveDirLoc);
					String fileName = file.getOriginalFilename();
					String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
					document.setFileReference(saveDirLoc + docId + fileExtension);
					if(documentRepository.save(document)!=null){
						logger.info(docId + " - Record(File Reference) in DB updated " + document.getFileReference());
						return document.getDocumentId();
					} else {
						throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"Insertion failed - inner");
					}
				} else {
		            throw new DestinationException(HttpStatus.BAD_REQUEST,"Failure : Empty File");
		        }
			} else {
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"Insertion failed - outer");
			}
	}

	
	private String getEntityId(DocumentRepositoryT document) throws Exception {
		String entityType = document.getEntityType();
		String customerId = document.getCustomerId();
		String opportunityId = document.getOpportunityId();
		String partnerId = document.getPartnerId();
		String taskId = document.getTaskId();
		String connectId = document.getConnectId();
		String entityId = null;
		if (EntityType.contains(entityType)) {
		switch(EntityType.valueOf(entityType)){
		case CUSTOMER:
			entityId = customerId;
			break;
		case PARTNER:
			entityId = partnerId;
			break;
		case OPPORTUNITY:
			entityId = opportunityId;
			break;
		case CONNECT:
			entityId = connectId;
			break;
		case TASK:
			entityId = taskId;
		}
		} else {
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"Invalid Entity Type");
		}
		return entityId;
		}

	private void validateInputs(DocumentRepositoryT document) throws Exception{
		String entityType = document.getEntityType();
		String customerId = document.getCustomerId();
		String opportunityId = document.getOpportunityId();
		String partnerId = document.getPartnerId();
		String taskId = document.getTaskId();
		String connectId = document.getConnectId();
		if (EntityType.contains(entityType)) {
		switch(EntityType.valueOf(entityType)){
		case CUSTOMER:
			if (customerId == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Customer ID can not be empty");
			}
			break;
		case PARTNER:
			if (partnerId == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Partner ID can not be empty");
			}
			break;
		case OPPORTUNITY:
			if (opportunityId == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Opportunity ID can not be empty");
			}
			break;
		case CONNECT:
			if (connectId == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Connect ID can not be empty");
			}
			break;
		case TASK:
			if (taskId == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Task ID can not be empty");
			}
			break;
		}
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Entity Type");
		}
	}

	private void saveFile(MultipartFile file, String saveDirLoc, String docId) throws IOException {
		try {
			byte[] bytes = file.getBytes();
			File dir = new File(saveDirLoc);
			if (!dir.exists())
	            dir.mkdirs();
			
			String fileName = file.getOriginalFilename();
			String saveFileName = docId;
            String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
			
			File serverFile = new File(dir.getAbsolutePath()
                    + File.separator+saveFileName+fileExtension);
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();
            
		} catch (IOException e) {
			throw e;
		}
		
		
		
	}

	private String getPathFromForm(String entityType,String entityId){
		StringBuffer saveDir = new StringBuffer("");
		saveDir.append(fileBasePath);
		saveDir.append(File.separator);
		saveDir.append(entityType);
		saveDir.append(File.separator);
		saveDir.append(entityId);
		saveDir.append(File.separator);
		return saveDir.toString();
	}

	@Transactional
	public String deleteDocRecords(String[] docIds) throws Exception{
		StringBuffer deletedRecords = new StringBuffer("");
		int index = 0;
		List<DocumentRepositoryT> docList = new ArrayList<DocumentRepositoryT>();
		StringBuffer missingIds = new StringBuffer("");
		for(String docId : docIds){
			DocumentRepositoryT docRep = documentRepository.findByDocumentId(docId);
			if(docRep!=null){
				logger.info(docId + " - Record Found");
				docList.add(docRep);
			}else {
				logger.info(docId + " - No Records Found");
				missingIds.append(docId + ",");
			}
		}
		
		if(missingIds.toString().isEmpty()){
			logger.info("All the required records found");
			index = 0;
			for(DocumentRepositoryT docRep : docList){
				index++;
				String fullPath = docRep.getFileReference();
				String id = docRep.getDocumentId();
				deleteFile(fullPath);
				logger.info(id + " - File deleted");
				documentRepository.delete(docRep);
				logger.info(id + " - record deleted");
				deletedRecords.append(id);
				if(index < docList.size()){
				deletedRecords.append(",");
				}
			}
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,"No records found for Ids : " +  missingIds.toString());
		}
		String delRecords = deletedRecords.toString();
		logger.info("Records deleted : " + delRecords);
		return delRecords;
	}
		
		public void deleteFile(String fullPath) throws Exception{
	    	try{
	    		 //String fullPath = fileBasePath+File.separator+"my.pdf";
	    		    File file = new File(fullPath);
	    		    if(file.delete()){
	    		    	logger.info(fullPath + " - File deleted");
	        		}else{
	        			logger.info(fullPath + " - File deletion Failed");
	        		}
	    	    } catch (Exception ex) {
	    	      //logger.info("Error writing file to output stream. Filename was '{}'", "", ex);
	    	      throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage() );
	    	    }
	    }
	
	
	
}
