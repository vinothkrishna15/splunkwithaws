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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DocumentRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;


/**
 * This service is used to save and delete documents
 * 
 */
@Service
public class DocumentService {

	@Autowired 
	DocumentRepository documentRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
	
	@Value("${fileBaseDir}")
	private String fileBasePath;

	/**
	 * This method is used to find the document using id
	 * @param documentId
	 * @return
	 * @throws Exception
	 */
	public DocumentRepositoryT findByDocumentId(String documentId) throws Exception{
		logger.debug("Start: Inside  findByDocumentId() of DocumentService");
		DocumentRepositoryT docRep = documentRepository.findByDocumentId(documentId);
		if(docRep==null){
			logger.error("NOT_FOUND: No Relevent Data/document Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data/document Found in the database");
		}
		logger.debug("End: Inside  findByDocumentId() of DocumentService");
		return docRep;
	}
	
	/**
	 * This method is used to save the document
	 * @param documentName
	 * @param documentType
	 * @param entityType
	 * @param parentEntity
	 * @param commentId
	 * @param connectId
	 * @param customerId
	 * @param opportunityId
	 * @param partnerId
	 * @param taskId
	 * @param uploadedBy
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public String saveDocument(String documentName,
			String documentType,
			String entityType, String parentEntity,
			String commentId, String connectId, String customerId,
			String opportunityId, String partnerId, String taskId,
			String uploadedBy, MultipartFile file) throws Exception {
		    logger.debug("Start: Inside  saveDocument() of DocumentService");
		    DocumentRepositoryT document=new DocumentRepositoryT();
		    document.setCommentId(commentId);
		    document.setConnectId(connectId);
		    document.setCustomerId(customerId);
		    document.setDocumentName(documentName);
		    document.setDocumentType(documentType);
			document.setEntityType(entityType);
			document.setFileReference(fileBasePath);
			document.setOpportunityId(opportunityId);
			document.setTaskId(taskId);
			document.setPartnerId(partnerId);
			
		    if(isDuplicateRecord(document)){
				throw new DestinationException(HttpStatus.BAD_REQUEST,"Failure : Duplicate found");
			} else {
				    document.setCommentId(null);
				    document.setConnectId(null);
				    document.setCustomerId(null);
				    document.setOpportunityId(null);
					document.setTaskId(null);
					document.setPartnerId(null);
			if(!commentId.equals(""))
			{
				logger.debug("commentId Not Empty");
				document.setCommentId(commentId);
			}			
			if(!connectId.equals(""))
			{
				logger.debug("connectId Not Empty");
				document.setConnectId(connectId);
			}
			if(!customerId.equals(""))
			{
				logger.debug("customerId Not Empty");
				document.setCustomerId(customerId);
			}
			document.setDocumentName(documentName);
            document.setDocumentType(documentType);
			document.setEntityType(entityType);
			document.setFileReference(fileBasePath);
			if(!opportunityId.equals(""))
			{
				logger.debug("opportunityId Not Empty");
				document.setOpportunityId(opportunityId);
			}
			
			document.setParentEntity(parentEntity);
			if(!partnerId.equals(""))
			{
				logger.debug("partnerId Not Empty");
				document.setPartnerId(partnerId);
			}			
			if(!taskId.equals(""))
			{
				logger.debug("taskId Not Empty");
				document.setTaskId(taskId);
			}
			UserT user=new UserT();
			user.setUserId(uploadedBy);
			document.setUserT(user);
			document.setUploadedBy(uploadedBy);
			document.setUploadedDatetime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            String docId = "";
			validateInputs(document);
			logger.debug("validated input(document record) for insertion");
			if(documentRepository.save(document)!=null){
				docId = document.getDocumentId();
				logger.debug("document record saved with id: " + docId);
				String entityId = getEntityId(document);
				logger.debug(docId + " - entity id: " + entityId);
				String saveDirLoc = getPathFromForm(entityType,entityId);
				logger.debug(docId + " - Directory to be stored : " + saveDirLoc);
				if (!file.isEmpty()) {
					saveFile(file,saveDirLoc,docId);
					logger.debug(docId + " - File saved at " + saveDirLoc);
					String fileName = file.getOriginalFilename();
					String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
					String relativePath = getRelativePath(entityType, entityId) + docId + fileExtension;
                    document.setFileReference(relativePath);
					if(documentRepository.save(document)!=null){
						logger.debug(docId + " - Record(File Reference) in DB updated " + document.getFileReference());
						return document.getDocumentId();
					} else {
						logger.error("INTERNAL_SERVER_ERROR: Insertion failed - inner");
						throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"Insertion failed - inner");
					}
				} else {
					logger.error("BAD_REQUEST: Failure : Empty File");
		            throw new DestinationException(HttpStatus.BAD_REQUEST,"Failure : Empty File");
		        }
			} else {
				logger.error("INTERNAL_SERVER_ERROR: Insertion failed - outer");
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"Insertion failed - outer");
			}
			}
		   
	}
    
	/**
	 * This method is used to validate duplicate records
	 * @param document
	 * @return
	 */
	private boolean isDuplicateRecord(DocumentRepositoryT document) {
	    logger.debug("Start: Inside  isDuplicateRecord() of DocumentService");
		boolean isDuplicate = false;
		String documentName = document.getDocumentName();
		String customerId = document.getCustomerId();
		String partnerId = document.getPartnerId();
		String connectId = document.getConnectId();
		String opportunityId = document.getOpportunityId();
		String taskId = document.getTaskId();
		
		DocumentRepositoryT doc = documentRepository.findDocument(documentName, customerId, partnerId, connectId, opportunityId, taskId);
		
		if(doc!=null){
			isDuplicate = true;
		}
		logger.debug("End: Inside  isDuplicateRecord() of DocumentService");
		return isDuplicate;
	}

	/**
	 * This method is used to fetch the relative path of the file
	 * @param entityType
	 * @param entityId
	 * @return
	 */
	private String getRelativePath(String entityType,String entityId){
		logger.debug("Start: Inside  getRelativePath() of DocumentService");
		StringBuffer relativePath = new StringBuffer("");
		relativePath.append(File.separator);
		relativePath.append(entityType);
		relativePath.append(File.separator);
		relativePath.append(entityId);
		relativePath.append(File.separator);
		logger.debug("End: Inside  getRelativePath() of DocumentService");
		return relativePath.toString();
	}
	
	/**
	 * This method is used to fetch the entity id 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	private String getEntityId(DocumentRepositoryT document) throws Exception {
		logger.debug("Inside getEntityId Service");
		String entityType = document.getEntityType();
		String customerId = document.getCustomerId();
		String opportunityId = document.getOpportunityId();
		String partnerId = document.getPartnerId();
		String taskId = document.getTaskId();
		String connectId = document.getConnectId();
		String entityId = null;
		if (EntityType.contains(entityType)) {
			logger.debug("Inside getEntityId Service");
		switch(EntityType.valueOf(entityType)){
		case CUSTOMER:
			logger.debug("Entity of Customer Found");
			entityId = customerId;
			break;
		case PARTNER:
			logger.debug("Entity of Partner Found");
			entityId = partnerId;
			break;
		case OPPORTUNITY:
			logger.debug("Entity of Opportunity Found");
			entityId = opportunityId;
			break;
		case CONNECT:
			logger.debug("Entity of Connect Found");
			entityId = connectId;
			break;
		case TASK:
			logger.debug("Entity of Task Found");
			entityId = taskId;
		}
		} else {
		logger.error("BAD_REQUEST: Invalid Entity Type");
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"Invalid Entity Type");
		}
		logger.debug("End: Inside  getEntityId() of DocumentService");
		return entityId;
		}
   /**
    * This method is used to validate the inputs provided
    * @param document
    * @throws Exception
    */
	private void validateInputs(DocumentRepositoryT document) throws Exception{
		logger.debug("Inside validateInputs Service");
		String entityType = document.getEntityType();
		String customerId = document.getCustomerId();
		String opportunityId = document.getOpportunityId();
		String partnerId = document.getPartnerId();
		String taskId = document.getTaskId();
		String connectId = document.getConnectId();
		if (EntityType.contains(entityType)) {
			logger.debug("EntityType is Present");
		switch(EntityType.valueOf(entityType)){
		case CUSTOMER:
			logger.debug("Customer is Found");
			if (customerId == null) {
				logger.error("BAD_REQUEST: Customer ID can not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Customer ID can not be empty");
			}
			break;
		case PARTNER:
			logger.debug("Partner is Found");
			if (partnerId == null) {
				logger.error("BAD_REQUEST: Partner ID can not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Partner ID can not be empty");
			}
			break;
		case OPPORTUNITY:
			logger.debug("Opportunity is Found");
			if (opportunityId == null) {
				logger.error("BAD_REQUEST: Opportunity ID can not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Opportunity ID can not be empty");
			}
			break;
		case CONNECT:
			logger.debug("Connect is Found");
			if (connectId == null) {
				logger.error("BAD_REQUEST: Connect ID can not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Connect ID can not be empty");
			}
			break;
		case TASK:
			logger.debug("Task is Found");
			if (taskId == null) {
				logger.error("BAD_REQUEST: Task ID can not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Task ID can not be empty");
			}
			break;
		}
		} else {
			logger.error("BAD_REQUEST: Invalid Entity Type");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Entity Type");
		}
		logger.debug("End: validateInputs() of Document Service");
	}

	private void saveFile(MultipartFile file, String saveDirLoc, String docId) throws IOException {
		logger.debug("Start: Inside saveFile() method of Document service");
		try {
			byte[] bytes = file.getBytes();
			File dir = new File(saveDirLoc);
			if (!dir.exists())
			{
				logger.debug("Directory does not exists");
				dir.mkdirs();
			}
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
			logger.error("Exception: "+e.getMessage());
			throw e;
		}
		logger.debug("End: saveFile() method of Document service");
	 }
	/**
	 * This method is used to construct the file path 
	 * @param entityType
	 * @param entityId
	 * @return
	 */

	private String getPathFromForm(String entityType,String entityId){
		logger.debug("Inside getPathFromForm Form");
		StringBuffer saveDir = new StringBuffer("");
		saveDir.append(fileBasePath);
		saveDir.append(File.separator);
		saveDir.append(entityType);
		saveDir.append(File.separator);
		saveDir.append(entityId);
		saveDir.append(File.separator);
		return saveDir.toString();
	}
	
	/**
	 * This method is used to delete the document records
	 * @param docIds
	 * @return
	 * @throws Exception
	 */
    @Transactional
	public String deleteDocRecords(String[] docIds) throws Exception{
		logger.debug("Start:Inside deleteDocRecords() of Document Service");
		StringBuffer deletedRecords = new StringBuffer("");
		int index = 0;
		List<DocumentRepositoryT> docList = new ArrayList<DocumentRepositoryT>();
		StringBuffer missingIds = new StringBuffer("");
		for(String docId : docIds){
			DocumentRepositoryT docRep = documentRepository.findByDocumentId(docId);
			if(docRep!=null){
				logger.debug(docId + " - Record Found");
				docList.add(docRep);
			}else {
				logger.debug(docId + " - No Records Found");
				missingIds.append(docId + ",");
			}
		}
		
		if(missingIds.toString().isEmpty()){
			logger.debug("All the required records found");
			index = 0;
			for(DocumentRepositoryT docRep : docList){
				index++;
				String fullPath = fileBasePath + docRep.getFileReference();
				String id = docRep.getDocumentId();
				deleteFile(fullPath);
				logger.debug(id + " - File deleted");
				documentRepository.delete(docRep);
				logger.debug(id + " - record deleted");
				deletedRecords.append(id);
				if(index < docList.size()){
				deletedRecords.append(",");
				}
			}
		} else {
			logger.error("NOT_FOUND: No records found for Ids" +  missingIds.toString());
			throw new DestinationException(HttpStatus.NOT_FOUND,"No records found for Ids : " +  missingIds.toString());
		}
		String delRecords = deletedRecords.toString();
		logger.debug("Records deleted : " + delRecords);
		logger.debug("End:Inside deleteDocRecords() of Document Service");
		return delRecords;
	}
	/**
	 * This method is used to delete the file	
	 * @param fullPath
	 * @throws Exception
	 */
		public void deleteFile(String fullPath) throws Exception{
			logger.debug("Start:Inside deleteFile() of Document Service");
	    	try{
	    		 //String fullPath = fileBasePath+File.separator+"my.pdf";
	    		    File file = new File(fullPath);
	    		    if(file.delete()){
	    		    	logger.debug(fullPath + " - File deleted");
	        		}else{
	        			logger.debug(fullPath + " - File deletion Failed");
	        		}
	    	    } catch (Exception ex) {
	    	    	logger.error("INTERNAL_SERVER_ERROR"+ex.getMessage());
	    	        throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage() );
	    	    }
	    	logger.debug("End:Inside deleteFile() of Document Service");
	    }
	
	
	
}
